package com.comedorsolidario.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import com.comedorsolidario.dao.*;
import com.comedorsolidario.model.*;
import com.comedorsolidario.util.*;
import static com.comedorsolidario.util.ValidacionUtil.*;

@WebServlet({"/logout"})
public class LogoutServlet extends BaseServlet {
    @Override
    protected void post(HttpServletRequest r, HttpServletResponse s) throws IOException {
        r.getSession().invalidate();
        ir(r, s, "/inicio", null);
    }
}
