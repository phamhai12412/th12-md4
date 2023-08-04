package rikkei.academy.service;

import rikkei.academy.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserServiceIMPL implements IUserService {
    private String jdbcURL = "jdbc:mysql://localhost:3306/demo";
    private String jdbcUsername = "root";
    private String jdbcPassword = "0812706565";

    // Các câu lệnh SQL
    private static final String INSERT_USERS_SQL = "INSERT INTO user" + "  (name, email, country) VALUES " +
            " (?, ?, ?);";

    private static final String SELECT_USER_BY_ID = "select id,name,email,country from user where id =?";
    private static final String SELECT_ALL_USERS = "select * from user";
    private static final String DELETE_USERS_SQL = "delete from user where id = ?;";
    private static final String UPDATE_USERS_SQL = "update user set name = ?,email= ?, country =? where id = ?;";

    public UserServiceIMPL() {
    }

    // Hàm để lấy kết nối cơ sở dữ liệu
    protected Connection getConnection() {
        Connection connection = null;
        try {
            // Load driver JDBC cho MySQL
            Class.forName("com.mysql.jdbc.Driver");
            // Tạo kết nối đến cơ sở dữ liệu
            connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
        } catch (SQLException e) {
            // In lỗi nếu có lỗi xảy ra trong quá trình kết nối
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // In lỗi nếu không tìm thấy driver JDBC
            e.printStackTrace();
        }
        return connection;
    }

    // Thêm người dùng mới vào cơ sở dữ liệu
    public void insertUser(User user) throws SQLException {
        System.out.println(INSERT_USERS_SQL);
        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USERS_SQL)) {
            // Thiết lập các tham số cho câu lệnh SQL INSERT
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getCountry());
            System.out.println(preparedStatement);
            // Thực thi câu lệnh SQL để thêm người dùng mới vào cơ sở dữ liệu
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            // In lỗi nếu có lỗi xảy ra trong quá trình thêm người dùng mới
            printSQLException(e);
        }
    }

    // Lấy thông tin người dùng dựa vào ID
    public User selectUser(int id) {
        User user = null;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select id,name,email,country from user where id =?");) {
            // Thiết lập tham số cho câu lệnh SQL SELECT theo ID
            preparedStatement.setInt(1, id);//mặc định là 1, set lại giá trị id
            System.out.println(preparedStatement);
            // Thực thi câu lệnh SQL để lấy kết quả trả về từ cơ sở dữ liệu dưới dạng ResultSet
            ResultSet rs = preparedStatement.executeQuery();
            // Xử lý ResultSet để tạo đối tượng User
            while (rs.next()) {
                String name = rs.getString("name");
                String email = rs.getString("email");
                String country = rs.getString("country");
                user = new User(id, name, email, country);
            }
        } catch (SQLException e) {
            // In lỗi nếu có lỗi xảy ra trong quá trình lấy thông tin người dùng
            printSQLException(e);
        }
        return user;
    }

    // Lấy danh sách tất cả người dùng
    public List<User> selectAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_USERS);) {
            System.out.println(preparedStatement);
            // Thực thi câu lệnh SQL để lấy danh sách tất cả người dùng dưới dạng ResultSet
            ResultSet rs = preparedStatement.executeQuery();
            // Xử lý ResultSet để tạo danh sách các đối tượng User
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String country = rs.getString("country");
                users.add(new User(id, name, email, country));
            }
        } catch (SQLException e) {
            // In lỗi nếu có lỗi xảy ra trong quá trình lấy danh sách người dùng
            printSQLException(e);
        }
        return users;
    }

    // Xóa người dùng dựa vào ID
    public boolean deleteUser(int id) throws SQLException {
        boolean rowDeleted;
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(DELETE_USERS_SQL);) {
            // Thiết lập tham số cho câu lệnh SQL DELETE theo ID
            statement.setInt(1, id);
            // Thực thi câu lệnh SQL để xóa người dùng dựa vào ID và kiểm tra số bản ghi bị xóa
            rowDeleted = statement.executeUpdate() > 0;
        }
        return rowDeleted;
    }

    // Cập nhật thông tin người dùng
    public boolean updateUser(User user) throws SQLException {
        boolean rowUpdated;
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(UPDATE_USERS_SQL);) {
            // Thiết lập các tham số cho câu lệnh SQL UPDATE
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getCountry());
            statement.setInt(4, user.getId());

            // Thực thi câu lệnh SQL để cập nhật thông tin người dùng và kiểm tra số bản ghi được cập nhật
            rowUpdated = statement.executeUpdate() > 0;
        }
        return rowUpdated;
    }

    // In thông tin lỗi SQL
    private void printSQLException(SQLException ex) {
        for (Throwable e : ex) {
            if (e instanceof SQLException) {
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }
}
