<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ page import="java.sql.*" %>
<html>
<head>
    <title>Course Content</title>
</head>
<body>
    <h2>Course Content</h2>
    <%
        int courseId = Integer.parseInt(request.getParameter("course_id"));
        int userId = (Integer) session.getAttribute("user_id");

        // Fetch the lessons and progress from the database
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String dbURL = "jdbc:mysql://localhost:3306/lms";
            String dbUser = "root";
            String dbPassword = "root";
            con = DriverManager.getConnection(dbURL, dbUser, dbPassword);

            String sql = "SELECT l.lesson_id, l.title, IFNULL(pt.is_completed, FALSE) AS is_completed " +
                         "FROM lesson l LEFT JOIN progress_tracking pt ON l.lesson_id = pt.lesson_id " +
                         "AND pt.user_id = ? WHERE l.course_id = ?";
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, courseId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                int lessonId = rs.getInt("lesson_id");
                String lessonTitle = rs.getString("title");
                boolean isCompleted = rs.getBoolean("is_completed");

                out.println("<div>");
                out.println("<h4>" + lessonTitle + "</h4>");
                if (!isCompleted) {
                    out.println("<form action='TrackProgressServlet' method='post'>");
                    out.println("<input type='hidden' name='lesson_id' value='" + lessonId + "'>");
                    out.println("<input type='hidden' name='user_id' value='" + userId + "'>");
                    out.println("<button type='submit'>Mark as Completed</button>");
                    out.println("</form>");
                } else {
                    out.println("<p>Lesson completed</p>");
                }
                out.println("</div>");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (con != null) con.close();
        }
    %>
</body>
	<%@ include file="additional/footer.jsp"%>

</html>
