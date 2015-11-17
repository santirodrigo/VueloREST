/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AD;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 *
 * @author Santi
 */
@Stateless
@Path("/VueloREST")
public class VueloREST {
    /* Dados un identificador de vuelo y una fecha, retorna el número de
    plazas que están libres */
    @GET
    @Path("/consulta_libres")
    public String consulta_libres (@QueryParam("id_vuelo") int id_vuelo,@QueryParam("fecha") int fecha) {
        //TODO write your implementation code here:
        Connection connection = null;
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(VueloREST.class.getName()).log(Level.SEVERE, null, ex);
        }
            try {
            connection = DriverManager.getConnection("jdbc:sqlite:F:\\UNI\\AD\\practica3.db");
            String selectStatement = "SELECT * " 
                                    + "FROM vuelo_fecha "
                                    + "WHERE id_vuelo=? AND fecha = ?";
            PreparedStatement prepStmt = connection.prepareStatement(selectStatement);
            
            prepStmt.setString(1,Integer.toString(id_vuelo));
            prepStmt.setString(2,Integer.toString(fecha));
            
            ResultSet rs = prepStmt.executeQuery();
            if (rs.next())
            {
                return String.valueOf(rs.getInt("num_plazas_max") - rs.getInt("num_plazas_ocupadas"));
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(VueloREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            try
            {
              if(connection != null)
                connection.close();
            }
            catch(SQLException e)
            {
              // connection close failed.
              System.err.println(e.getMessage());
            }
        }
        return "-1";
    }

    /* Dados un identificador de vuelo y una fecha, reserva una plaza si
    quedan plazas libres (incrementa el número de plazas ocupadas en un
    vuelo en una fecha.
    Si es posible realizar la reserva, esta operación retorna el número de
    plazas ocupadas que hay en el vuelo.
    Si no es posible realizar la reserva, esta operación retorna -1. */
    @POST
    @Path("/reserva_plaza")
    public String reserva_plaza (@FormParam("id_vuelo") int id_vuelo, @FormParam("fecha") int fecha) {
        Connection connection = null;
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(VueloREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:F:\\UNI\\AD\\practica3.db");
            String selectStatement = "SELECT * " 
                                    + "FROM vuelo_fecha "
                                    + "WHERE id_vuelo=? AND fecha = ?";
            PreparedStatement prepStmt = connection.prepareStatement(selectStatement);
            
            prepStmt.setString(1,Integer.toString(id_vuelo));
            prepStmt.setString(2,Integer.toString(fecha));
            ResultSet rs = prepStmt.executeQuery();
            if (rs.next())
            {
                if (rs.getInt("num_plazas_max") - rs.getInt("num_plazas_ocupadas") > 0 )
                {
                    int plazasOcupadas = rs.getInt("num_plazas_ocupadas")+1;
                    selectStatement = "UPDATE vuelo_fecha " 
                                    + "SET num_plazas_ocupadas=? "
                                    + "WHERE id_vuelo=? AND fecha = ?";
                    prepStmt = connection.prepareStatement(selectStatement);
                    prepStmt.setString(1,Integer.toString(plazasOcupadas));
                    prepStmt.setString(2,Integer.toString(id_vuelo));
                    prepStmt.setString(3,Integer.toString(fecha));
                    prepStmt.executeUpdate();
                    return String.valueOf(plazasOcupadas);
                }
                else
                {
                    return "-1";
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(VueloREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            try
            {
              if(connection != null)
                connection.close();
            }
            catch(SQLException e)
            {
              // connection close failed.
              System.err.println(e.getMessage());
            }
        }
        return "0";
    }
}
