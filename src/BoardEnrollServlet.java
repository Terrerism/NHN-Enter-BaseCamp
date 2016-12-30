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
import java.sql.PreparedStatement;

/**
 * Created by show9 on 2016-12-31.
 */
@WebServlet("/board/enroll")
public class BoardEnrollServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            ServletContext servletContext = this.getServletContext();
            Class.forName(servletContext.getInitParameter("driver"));
            connection = DriverManager.getConnection(
                    servletContext.getInitParameter("url"),
                    servletContext.getInitParameter("username"),
                    servletContext.getInitParameter("password"));
            preparedStatement = connection.prepareStatement(
                    "INSERT INTO BOARDS(EMAIL,PWD,CONTENT,CRE_TIME,MOD_TIME)" +
                            " VALUE (?,?,?,NOW(),NOW())");
            preparedStatement.setString(1, request.getParameter("email"));
            preparedStatement.setString(2, request.getParameter("password"));
            preparedStatement.setString(3, request.getParameter("content"));
            preparedStatement.executeUpdate();

            response.setContentType("text/html; charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<html><head><title>방명록 글 등록 결과</title></head>");
            out.println("<body>");
            out.println("<p>등록 성공입니다!</p>");
            out.println("</body></html>");

            // 1초 뒤에 새로고침, url은 list로
            response.addHeader("Refresh", "1;url=list");
        } catch (Exception e) {
            throw new ServletException(e);
        } finally {
            try {if (preparedStatement != null) preparedStatement.close();} catch (Exception e) {}
            try {if (connection != null) connection.close();} catch (Exception e) {}
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<html><head><title>방명록 글쓰기</title></head>");
        out.println("<body><h1>방명록 글쓰기</h1>");
        out.println("<form action='enroll' method='post'>");
        out.println("이메일: <input type='text' name='email'><br>");
        out.println("암호: <input type='password' name='password'><br>");
        out.println("내용: <input type='text' name='content'><br>");
        out.println("<input type='submit' value='등록'>");
        out.println("<input type='reset' value='취소'>");
        out.println("</form>");
        out.println("</body></html>");
    }
}
