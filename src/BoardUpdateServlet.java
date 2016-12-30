import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

/**
 * Created by show9 on 2016-12-31.
 */
@WebServlet("/board/update")
public class BoardUpdateServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // no, content, password를 받는다
        request.setCharacterEncoding("UTF-8");
        Connection connection = null;
        PreparedStatement preparedStatement = null;
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
                    "select PWD from BOARDS" +
                            " where BNO=" + request.getParameter("no"));
            resultSet.next();

            // 입력받은 비밀번호와 디비의 비밀번호가 같으면
            // 글 수정을 수행뒤 list로 돌아감
            if(resultSet.getString("PWD").compareTo(request.getParameter("password")) == 0) {
                preparedStatement = connection.prepareStatement(
                        "update BOARDS set CONTENT=?, MOD_TIME=now()"
                        + " where BNO=?");
                preparedStatement.setString(1, request.getParameter("content"));
                preparedStatement.setString(2, request.getParameter("no"));

                preparedStatement.executeUpdate();
                response.sendRedirect("list");
            }
            // 입력받은 비밀번호와 디비의 비밀번호가 다르면
            // update로 돌아가 다시 입력받음
            else {
                response.setContentType("text/html; chatset=UTF-8");
                PrintWriter out = response.getWriter();
                out.println("<html><head><title>글 수정 요청 결과</title></head>");
                out.println("<body>");
                out.println("<p>비밀번호를 다시 입력하세요</p>");
                out.println("</body></html>");

                response.addHeader("Refresh", "1;url=update?no=" + request.getParameter("no"));
            }

        } catch (Exception e) {
            throw new ServletException(e);
        } finally {
            try {if(statement != null) statement.close();} catch (Exception e) {}

        }
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
                    "select BNO,CONTENT,PWD,CRE_TIME from BOARDS" +
                            " where BNO=" + request.getParameter("no"));
            resultSet.next();

            response.setContentType("text/html; chatset=UTF-8");
            PrintWriter out = response.getWriter();

            // POST요청으로
            // no, content, password를 날린다
            out.println("<html><head><title>글 수정</title></head>");
            out.println("<body><h1>방명록 글 수정</h1>");
            out.println("<form action='update' method='post'>");
            // 이걸 꼭 UI에 보여줘야 하나..?
            // 안보여주고 POST 파라미터로 넣는 방법이 있을 것이다.
            out.println("글 등록번호: <input type='text' name='no' value='" +
                        request.getParameter("no") + " 'readonly><br>");

            out.println("등록 시간: " + resultSet.getTimestamp("CRE_TIME") + "<br>");
            out.println("글 내용: <input type='text' name='content' value='" +
                        resultSet.getString("CONTENT") + "'><br>");
            out.println("암호: <input type='password' name='password'><br>");
            out.println("<input type='submit' value='저장'>");
            out.println("<input type='button' value='취소'" +
                        " onclick='location.href=\"list\"'>");
            out.println("</form>");
            out.println("</body></html>");

        } catch (Exception e) {
            throw new ServletException(e);
        } finally {
            try {if(resultSet != null) resultSet.close();} catch (Exception e) {}
            try {if(statement != null) statement.close();} catch (Exception e) {}
            try {if(connection != null) connection.close();} catch (Exception e) {}
        }
    }
}
