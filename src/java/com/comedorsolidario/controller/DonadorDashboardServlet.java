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

@WebServlet({"/donante/dashboard"})
public class DonadorDashboardServlet extends BaseServlet {
    @Override
    protected void get(HttpServletRequest r, HttpServletResponse s)
            throws ServletException, IOException, SQLException {
        r.setAttribute("resumen", new DonacionDAO().resumen(usuarioId(r)));
        vista(r, s, "donante/dashboard.jsp");
    }
}
