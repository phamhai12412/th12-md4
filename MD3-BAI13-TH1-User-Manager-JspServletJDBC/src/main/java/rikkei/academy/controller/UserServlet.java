package rikkei.academy.controller;

import rikkei.academy.model.User;
import rikkei.academy.service.IUserService;
import rikkei.academy.service.UserServiceIMPL;

import java.io.*;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet("/users")
public class UserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private IUserService userService;

    public void init() {
        userService = new UserServiceIMPL();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }

        try {
            switch (action) {
                case "create":
                    // Hiển thị trang tạo mới người dùng
                    showNewForm(request,response);
                    break;
                case "edit":
                    // Hiển thị trang chỉnh sửa thông tin người dùng
                    showEditForm(request, response);
                    break;
                case "delete":
                    // Xóa người dùng
                    deleteUser(request, response);
                    break;
                default:
                    // Hiển thị danh sách người dùng
                    listUser(request, response);
                    break;
            }
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");
        String action = req.getParameter("action");
        if (action == null) {
            action = "";
        }
        try {
            switch (action) {
                case "create":
                    // Thêm mới người dùng
                    insertUser(req, resp);
                    break;
                case "edit":
                    // Cập nhật thông tin người dùng
                    updateUser(req, resp);
                    break;
            }
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }

    }

    // Lấy danh sách người dùng và hiển thị trên trang JSP
    private void listUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        List<User> listUser = userService.selectAllUsers();
        // Đặt danh sách người dùng vào thuộc tính của request để truyền sang trang JSP
        request.setAttribute("listUser", listUser);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/user/list.jsp");
        dispatcher.forward(request, response);
    }

    // Hiển thị trang tạo mới người dùng
    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/user/create.jsp");
        dispatcher.forward(request, response);
    }

    // Hiển thị trang chỉnh sửa thông tin người dùng
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        // Lấy thông tin người dùng dựa vào ID
        User existingUser = userService.selectUser(id);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/user/edit.jsp");
        // Đặt thông tin người dùng vào thuộc tính của request để truyền sang trang JSP
        request.setAttribute("user", existingUser);
        dispatcher.forward(request, response);
    }

    // Thêm mới người dùng vào cơ sở dữ liệu
    private void insertUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String country = request.getParameter("country");
        // Tạo đối tượng User mới từ thông tin người dùng nhập vào
        User newUser = new User(name, email, country);
        // Thêm người dùng mới vào cơ sở dữ liệu
        userService.insertUser(newUser);
        // Chuyển hướng về trang danh sách người dùng sau khi thêm mới thành công
        response.sendRedirect(request.getContextPath() + "/users");
    }

    // Cập nhật thông tin người dùng vào cơ sở dữ liệu
    private void updateUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        int id = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String country = request.getParameter("country");

        // Tạo đối tượng User mới với thông tin cập nhật từ người dùng
        User user = new User(id, name, email, country);
        // Cập nhật thông tin người dùng vào cơ sở dữ liệu
        userService.updateUser(user);
        // Chuyển hướng về trang danh sách người dùng sau khi cập nhật thành công
        response.sendRedirect(request.getContextPath() + "/users");
    }

    // Xóa người dùng khỏi cơ sở dữ liệu
    private void deleteUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        int id = Integer.parseInt(request.getParameter("id"));
        // Xóa người dùng dựa vào ID
        userService.deleteUser(id);

        // Lấy lại danh sách người dùng sau khi đã xóa
        List<User> listUser = userService.selectAllUsers();
        // Đặt danh sách người dùng vào thuộc tính của request để truyền sang trang JSP
        request.setAttribute("listUser", listUser);
        // Chuyển hướng về trang danh sách người dùng sau khi xóa thành công
        response.sendRedirect(request.getContextPath() + "/users");
    }

    public void destroy() {
    }
}
