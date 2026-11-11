import java.sql.*;
import com.comedorsolidario.util.ConexionBD;
class InspeccionarEsquema {
 public static void main(String[] args) throws Exception {
  try(Connection c=ConexionBD.getConnection()) {
   System.out.println("MySQL "+c.getMetaData().getDatabaseProductVersion());
   try(PreparedStatement p=c.prepareStatement("SELECT TABLE_NAME,COLUMN_NAME,COLUMN_TYPE,IS_NULLABLE FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() ORDER BY TABLE_NAME,ORDINAL_POSITION"); ResultSet r=p.executeQuery()) {while(r.next()) System.out.println(r.getString(1)+"."+r.getString(2)+" "+r.getString(3)+" nullable="+r.getString(4));}
  }
 }
}
