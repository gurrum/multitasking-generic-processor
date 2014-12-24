package org.processor.service;

public interface Processor<T,U> {
U process(T arg);
}
