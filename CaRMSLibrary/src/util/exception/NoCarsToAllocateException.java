/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author Nelson Choo
 */
public class NoCarsToAllocateException extends Exception {

    /**
     * Creates a new instance of <code>NoCarsToAllocateException</code> without
     * detail message.
     */
    public NoCarsToAllocateException() {
    }

    /**
     * Constructs an instance of <code>NoCarsToAllocateException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NoCarsToAllocateException(String msg) {
        super(msg);
    }
}
