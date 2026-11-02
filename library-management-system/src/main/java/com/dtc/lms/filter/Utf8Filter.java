package com.dtc.lms.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;

/**
 * Forces UTF-8 for every request and response.
 *
 * <p>Without this, a name typed as "Anushka" is fine but a rupee sign or an
 * accented letter could arrive mangled. It must run <b>before</b> any servlet
 * reads a parameter, which is why the filters are ordered in {@code web.xml}
 * (UTF-8 first, authentication second).</p>
 */
public class Utf8Filter implements Filter {

    private static final String UTF_8 = "UTF-8";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        request.setCharacterEncoding(UTF_8);
        response.setCharacterEncoding(UTF_8);
        chain.doFilter(request, response);
    }
}
