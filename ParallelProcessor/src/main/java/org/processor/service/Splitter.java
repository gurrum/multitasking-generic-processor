package org.processor.service;

public interface Splitter<T,V> {
 	V[] split(T arg);
}
