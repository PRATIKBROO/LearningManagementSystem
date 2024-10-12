package com.org.dao;
	import java.io.File;
	import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
	import java.sql.DriverManager;
	import java.sql.PreparedStatement;
	import java.sql.SQLException;
	import javax.servlet.ServletException;
	import javax.servlet.annotation.MultipartConfig;
	import javax.servlet.annotation.WebServlet;
	import javax.servlet.http.HttpServlet;
	import javax.servlet.http.HttpServletRequest;
	import javax.servlet.http.HttpServletResponse;
	import javax.servlet.http.Part;

	@WebServlet("/UploadMaterialServlet")
	@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
	                 maxFileSize = 1024 * 1024 * 50,      // 50MB
	                 maxRequestSize = 1024 * 1024 * 100)  // 100MB
	public class UploadMaterialServlet extends HttpServlet {
	    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	        int courseId = Integer.parseInt(request.getParameter("course_id"));
	        Part filePart = request.getPart("file");
	        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
	        String fileType = filePart.getContentType();

	        // Save file to server
	        String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
	        File uploadDir = new File(uploadPath);
	        if (!uploadDir.exists()) uploadDir.mkdir();

	        String filePath = uploadPath + File.separator + fileName;
	        filePart.write(filePath);

	        // Insert file metadata into database
	        String dbURL = "jdbc:mysql://localhost:3306/lms";
	        String dbUser = "root";
	        String dbPassword = "root";

	        try (Connection con = DriverManager.getConnection(dbURL, dbUser, dbPassword)) {
	            String sql = "INSERT INTO learning_materials (course_id, file_name, file_type, file_path) VALUES (?, ?, ?, ?)";
	            PreparedStatement pstmt = con.prepareStatement(sql);
	            pstmt.setInt(1, courseId);
	            pstmt.setString(2, fileName);
	            pstmt.setString(3, fileType);
	            pstmt.setString(4, filePath);
	            pstmt.executeUpdate();

	            con.close();
	            // Redirect to manage materials page
	            response.sendRedirect("manageMaterials.jsp");
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	}


