import dao.OrderHeaderDAO;
import dao.DiningTableDAO;
import model.DiningTable;
import model.OrderHeader;

import java.time.LocalDateTime;

public class Runner {
    public static void main(String[] args) {
        DiningTableDAO diningTableDAO = new DiningTableDAO();
        OrderHeaderDAO orderDAO = new OrderHeaderDAO();

        // 1. Create a new table
        DiningTable table = new DiningTable();
        table.setTableNumber(8);
        table.setStatus("Available");
        table.setSeatingCapacity(4);
        diningTableDAO.create(table);

        // 2. Create a new order
        OrderHeader order = new OrderHeader();
        order.setDiningTable(table);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("Pending");
        order.setSubTotal(200.00);
        orderDAO.create(order);

        // 3. Find table by ID
        DiningTable foundTable = diningTableDAO.findById(table.getId());
        System.out.println("Found Table: ID = " + foundTable.getId() + ", Number = " + foundTable.getTableNumber());

        // 4. Find all orders
        System.out.println("All Orders:");
        orderDAO.findAll().forEach(o -> System.out.println("Order ID: " + o.getId() + ", SubTotal: " + o.getSubTotal()));

        // 5. Update table status
        foundTable.setStatus("Occupied");
        diningTableDAO.update(foundTable);
    }
}
