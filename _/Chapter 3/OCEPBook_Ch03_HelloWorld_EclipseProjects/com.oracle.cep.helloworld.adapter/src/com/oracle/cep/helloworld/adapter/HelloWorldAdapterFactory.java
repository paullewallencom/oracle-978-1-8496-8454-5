package com.oracle.cep.helloworld.adapter;

import com.bea.wlevs.ede.api.Adapter;
import com.bea.wlevs.ede.api.AdapterFactory;

public class HelloWorldAdapterFactory implements AdapterFactory {

	@Override
	public Adapter create() throws IllegalArgumentException {
		
		return new HelloWorldAdapter();
	}	
	
}


