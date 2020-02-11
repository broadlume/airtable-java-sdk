/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Sybit GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 */
package com.sybit.airtable;

import com.sybit.airtable.movies.Actor;
import org.junit.Assert;
import org.junit.Test;

public class TableTest {

    private Airtable airtable = new Airtable(Configuration.builder().apiKey("123").endpointUrl("https://url").build());
    private Base base = new Base("base", airtable);

    @Test
    public void key2properties() {
        Table<Actor> table = new Table<>("table", Actor.class, base);

        String actual = table.key2property("FirstName");
        Assert.assertEquals("firstName", actual);

        actual = table.key2property("First-Name");
        Assert.assertEquals("first-Name", actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void key2properties_EmptyArgument() {
        Table<Actor> table = new Table<>("table", Actor.class, base);
        table.key2property("");
        Assert.fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void key2properties_NullArgument() {
        Table<Actor> table = new Table<>("table", Actor.class, base);
        table.key2property(null);
        Assert.fail();
    }

}
