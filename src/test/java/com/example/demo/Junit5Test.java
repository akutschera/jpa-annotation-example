package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Created by Andreas Kutschera.
 */
public class Junit5Test {

    @Test
    @DisplayName( "geiler scheiss" )
    void coolShit() {
        assertThat( 2 ).isLessThan( 3 );
    }

}
