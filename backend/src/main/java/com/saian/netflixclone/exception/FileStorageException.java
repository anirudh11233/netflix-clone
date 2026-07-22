package com.saian.netflixclone.exception;

/** Bad upload — empty file, wrong type, or a disk write problem. */
public class FileStorageException extends RuntimeException {

    public FileStorageException(String message) {
        super(message);
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
