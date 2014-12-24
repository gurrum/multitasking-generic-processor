package org.processor.service;

public interface Aggregator<U,W> {
U aggregate(W[] args);
}
