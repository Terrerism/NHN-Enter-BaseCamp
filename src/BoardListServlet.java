import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by show9 on 2016-12-31.
 */
@WebServlet("/board/list")
public class BoardListServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            ServletContext servletContext = this.getServletContext();
            Class.forName(servletContext.getInitParameter("driver"));
            connection = DriverManager.getConnection(
                    servletContext.getInitParameter("url"),
                    servletContext.getInitParameter("username"),
                    servletContext.getInitParameter("password"));
            statement = connection.createStatement();
            resultSet = statement.executeQuery(
                    "select BNO,EMAIL,CONTENT,CRE_TIME,MOD_TIME" +
                            " from BOARDS" +
                            " order by CRE_TIME DESC");
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<html><head><title>방명록</title></head>");
            out.println("<body><h1>방명록</h1>");
            out.println("<p><a href='enroll'>글쓰기</a></p>");
            int num = 1;
            while (resultSet.next()) {
                out.println(
                        num + "," +
                        resultSet.getString("EMAIL") + "," +
                        resultSet.getString("CONTENT") + "," +
                        resultSet.getTimestamp("CRE_TIME") + "," +
                        resultSet.getTimestamp("MOD_TIME") + "," +
                        "<a href='update?no=" + resultSet.getInt("BNO") + "'>수정</a><br>");
                num++;
            }
            out.println("</body></html>");
        } catch (Exception e) {
            throw new ServletException(e);
        } finally {
            try {if (resultSet != null) resultSet.close();} catch (Exception e) {}
            try {if (statement != null) statement.close();} catch (Exception e) {}
            try {if (connection != null) connection.close();} catch (Exception e) {}
        }
    }
}
