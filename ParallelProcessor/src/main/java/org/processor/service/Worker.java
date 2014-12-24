package org.processor.service;

public interface Worker<V,W> {
W processPart(V part);
}
