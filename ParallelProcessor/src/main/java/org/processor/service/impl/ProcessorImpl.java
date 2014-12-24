package org.processor.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;

import org.processor.service.Aggregator;
import org.processor.service.Processor;
import org.processor.service.Splitter;
import org.processor.service.Worker;
import org.springframework.beans.factory.annotation.Autowired;

public class ProcessorImpl<T, U, V, W> implements Processor<T, U> {
    
	private final static int MAX_THREADS = 5;
	
	@Autowired
	Splitter<T,V> splitter;
	
	@Autowired
	Aggregator<U, W> aggregator;
	
	@Autowired
	Worker<V,W> worker;
	
	
	@SuppressWarnings("unchecked")
	@Override
	public U process(T arg) {
		
		if(null == arg){
			throw new RuntimeException("Process Input is null");
		}
		V[] parts = splitter.split(arg);
		
		if(null == parts || parts.length == 0) {
			throw new RuntimeException("No parts availables");
		}
		
		ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREADS);
		List<Future<W>> futures = new ArrayList<Future<W>>();
		
		for(V part: parts){
			futures.add(executorService.submit(new Callable<W>(){
				public W call(){
					return worker.processPart(part);
				}
			}));
		}
		
		List<W> partialResultList = new ArrayList<W>(parts.length);
		
		for (Future<W> future : futures){
			try {
				partialResultList.add(future.get());
			} catch (InterruptedException e) {
				throw new RuntimeException(e.getMessage());
			} catch (ExecutionException e) {
				throw new RuntimeException(e.getMessage());
			}
		}
		
		return aggregator.aggregate((W[]) partialResultList.toArray());
		
	}

	
}
