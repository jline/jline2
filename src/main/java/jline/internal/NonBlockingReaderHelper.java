/*
 * Copyright (c) 2002-2012, the original author or authors.
 *
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package jline.internal;

import java.io.IOException;
import java.io.Reader;

/**
 * This class is used for non-blocking input from a {@link Reader}.
 * It maintains an internal thread that calls {@link Reader#read()}
 * to wait for the next character.
 *
 * <p>An instance of this class can be in one of the following states:
 * <dl style="margin-left:20px">
 *  <dt>READING
 *  <dd>The internal thread is blocked within a {@link Reader#read()} call.
 *  <dt>READY
 *  <dd>A character has been received from the underlying Reader
 *      (or an Exception has occurred while reading).<br>
 *      The internal thread is idle.
 *  <dt>IDLE
 *  <dd>The internal thread is idle and no character or Exception is pending.
 * </dl>
 *
 * @author <a href="mailto:chdh@inventec.ch">Christian d'Heureuse</a>
 */
public class NonBlockingReaderHelper
{
    private Reader           in;                 // underlying reader
    private State            state;              // internal state
    private boolean          isShutdown;         // true after shutdown() has been called
    private IOException      pendingException;
    private int              pendingChar;

    // Internal state of the NonBlockingReaderHelper.
    // The states are documented in the class description of NonBlockingHeader.
    private enum State { READING, READY, IDLE }

    /**
     * Creates a <code>NonBlockingReaderHelper</code>.
     * The internal thread is started and the state is set to <code>IDLE</code>.
     */
    public NonBlockingReaderHelper (Reader in) {
        this.in = in;
        state = State.IDLE;
        Thread thread = new Thread() {
            @Override public void run() {
                threadMain();
            }
        };
        thread.setDaemon(true);
        thread.setName("JLine NonBlockingReaderHelper thread");
        thread.start();
    }

    /**
     * Shuts down the internal thread that is handling blocking I/O.
     * Note that if the internal thread is currently blocked within a {@link Reader#read()} call,
     * the thread cannot actually shut down until the call completes.
     */
    public synchronized void shutdown() {
        if (isShutdown) {
            return;
        }
        isShutdown = true;
        notify();
    }

    /**
     * Reads a character from the underlying <code>Reader</code>.
     *
     * <p>The following program logic is used (simplified):
     * <pre>
     *    if (wait) {                          // blocking read
     *
     *       switch (state) {
     *
     *          case IDLE:
     *             return in.read();           // the internal thread is bypassed
     *             // state remains IDLE
     *
     *          case READING:
     *             ... wait until the thread completes in.read() ...
     *             state = IDLE;
     *             ... return the character read by the internal thread ...
     *
     *          case READY:
     *             state = IDLE;
     *             ... return the character read by the internal thread ...
     *
     *       }
     *    } else {                             // non-blocking read
     *
     *       switch (state) {
     *
     *          case IDLE:
     *             state = READING;
     *             return -2;
     *             // the internal thread will call in.read()
     *
     *          case READING:
     *             return -2;
     *             // state remains READING
     *
     *          case READY:
     *             state = IDLE;
     *             ... return the character read by the internal thread ...
     *
     *       }
     *    }</pre>
     *
     * @param wait
     *     <code>true</code> to block and wait for the next character to become available, or
     *     <code>false</code> to return immediately, whether or not a character is available.
     * @return
     *     The character read,
     *     -1 if EOF is reached, or
     *     -2 if no character is ready (occurs only when <code>wait</code> is <code>false</code>).
     * @throws IOException
     *     when an I/O error has occurred while reading from the underlying <code>Reader</code>.
     */
    public int read(boolean wait) throws IOException {
        if (wait) {
            return readWait();
        } else {
            return readNoWait();
        }
    }

    private int readWait() throws IOException {
        synchronized (this) {
            while (true) {
                verifyNotShutdown();
                if (state == State.IDLE) {
                    break;
                    // in.read() will be called below, outside the synchronized block.
                }
                if (state == State.READY) {
                    return consume();
                }
                assert state == State.READING;
                try {
                   wait();
                }
                catch (InterruptedException e) {
                   throw new IOException("Unexpected interruption.");
                }
            }
        }
        // The blocking in.read() is called here, outside of the synchronized block,
        // to allow other threads to call NonBlockingReaderHelper.read(false).
        return in.read();
    }

    private synchronized int readNoWait() throws IOException {
        verifyNotShutdown();
        switch (state) {
            case IDLE:
                state = State.READING;
                notify();
                return -2;
            case READING:
                return -2;
            case READY:
                return consume();
            default:
                throw new AssertionError();
        }
    }

    private synchronized int consume() throws IOException {
        assert state == State.READY;
        state = State.IDLE;
        notify();
        if (pendingException != null) {
            throw pendingException;
        }
        return pendingChar;
    }

    private synchronized void verifyNotShutdown() {
        if (isShutdown) {
            throw new IllegalStateException("This NonBlockingReaderHelper has been shut down.");
        }
    }

    // The main routine of the internal thread.
    private void threadMain() {
        while (true) {

            boolean doRead = false;

            synchronized (this) {
                if (isShutdown) {
                    break;
                }
                switch (state) {
                   case READING:
                      doRead = true;
                      break;
                   default:
                      try {
                          wait();
                      }
                      catch (InterruptedException e) {
                          isShutdown = true;
                          notify();
                      }
                }
            }

            if (doRead) {
                threadRead();
            }

        }
    }

   // Reads from the underlying Reader, on the internal thread.
   private void threadRead() {
       try {
           pendingChar = in.read();
           pendingException = null;
       }
       catch (IOException e) {
           pendingException = e;
       }
       synchronized (this) {
           assert state == State.READING;
           state = State.READY;
           notify();
       }
   }

}
