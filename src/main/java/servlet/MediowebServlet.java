package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.MedicoJpaController;
import dto.Medico;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.HashPass;


@WebServlet(name = "MediowebServlet", urlPatterns = {"/medioscrud"})
public class MediowebServlet extends HttpServlet {

    private final MedicoJpaController medioDAO = new MedicoJpaController();
    private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            List<Medico> listaMedios = medioDAO.findMedicoEntities();
            JSONArray jsonArray = new JSONArray();

            for (Medico medio : listaMedios) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("codiMedi", medio.getCodiMedi());
                jsonObject.put("ndniMedi", medio.getNdniMedi());
                jsonObject.put("appaMedi", medio.getAppaMedi());
                jsonObject.put("apmaMedi", medio.getApmaMedi());
                jsonObject.put("nombMedi", medio.getNombMedi());

                if (medio.getFechNaciMedi() != null) {
                    String fechaFormateada = new SimpleDateFormat("yyyy-MM-dd").format(medio.getFechNaciMedi());
                    jsonObject.put("fechNaciMedi", fechaFormateada);
                } else {
                    jsonObject.put("fechNaciMedi", JSONObject.NULL);
                }

                jsonObject.put("logiMedi", medio.getLogiMedi());
                

                jsonArray.put(jsonObject);
            }

            response.getWriter().write(jsonArray.toString());

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error al obtener los datos: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        try {
            BufferedReader reader = request.getReader();
            Medico nuevoMedio = gson.fromJson(reader, Medico.class);
            nuevoMedio.setPassMedi(HashPass.hashPassword(nuevoMedio.getPassMedi()));
            medioDAO.create(nuevoMedio);

            response.getWriter().write(new JSONObject().put("message", "Medio creado exitosamente").toString());

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(new JSONObject().put("error", "Error al crear medio: " + e.getMessage()).toString());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        try {
            BufferedReader reader = request.getReader();
            Medico medioActualizado = gson.fromJson(reader, Medico.class);
            medioActualizado.setPassMedi(HashPass.hashPassword(medioActualizado.getPassMedi()));
            medioDAO.edit(medioActualizado);

            response.getWriter().write(new JSONObject().put("message", "Medio actualizado exitosamente").toString());

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(new JSONObject().put("error", "Error al actualizar medio: " + e.getMessage()).toString());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        try {
            String codiMediStr = request.getParameter("codiMedi");
            if (codiMediStr == null) {
                throw new IllegalArgumentException("Código de medio no proporcionado");
            }

            Integer codiMedi = Integer.parseInt(codiMediStr);
            medioDAO.destroy(codiMedi);

            response.getWriter().write(new JSONObject().put("message", "Medio eliminado exitosamente").toString());

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(new JSONObject().put("error", "Error al eliminar medio: " + e.getMessage()).toString());
        }
    }

    @Override
    public String getServletInfo() {
        return "Servlet para CRUD de Medioweb";
    }
}
