/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Sybit GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 */
package com.sybit.airtable;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 *
 * @author fzr
 */
public class BaseTest {
    
    private Airtable airtable;
    
    @Before
    public void before() {
        this.airtable = new Airtable(Configuration.builder().setApiKey("123").setEndpointUrl("http://localhost").build());
    }
    
    @Test
    public void baseTest(){
    
        Base base = new Base("base", this.airtable);
        assertNotNull(base);        
    }
    
    
    @Test
    public void airtableTest(){
    
        Base base = new Base("base", this.airtable);
        assertEquals(base.airtable(),this.airtable);
    }
    
    @Test
    public void baseNameTest(){
        
        Base base = new Base("base", this.airtable);
        assertEquals(base.name(),"base");
    }

    @Test(expected = NullPointerException.class )
    public void baseAssertationTest() {
        new Base(null,null);
    }
}
