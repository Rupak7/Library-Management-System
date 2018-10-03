/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BookList;

import DButils.ConnectDB;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;


public class BookListController implements Initializable {

    @FXML
    private TableView<Book> tableview;
    @FXML
    private TextField searchId;
    @FXML
    private TableColumn<Book, String> titlecol;
    @FXML
    private TableColumn<Book, String> idcol;
    @FXML
    private TableColumn<Book, String> authorcol;
    @FXML
    private TableColumn<Book, String> pubcol;

    private ConnectDB connectDB;
    
    ObservableList<Book> list= FXCollections.observableArrayList();
    FilteredList<Book> filterdata = new FilteredList<>(list,e->true);
    private String sql_books;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initcol();
        connectDB = new ConnectDB();
        LoadData();
        
        tableview.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
    }
            public void startBook() {
            initcol();
            connectDB = new ConnectDB();
            LoadData();
        }
    public void deleteButtonpushed()
    {
        ObservableList<Book> selectedRows,allBooks;
        allBooks = tableview.getItems();
        
        selectedRows = tableview.getSelectionModel().getSelectedItems();
        
        for(Book books:selectedRows)
        {
            allBooks.remove(books);
        }
    }
    public void initcol(){
        titlecol.setCellValueFactory(new PropertyValueFactory<>("title"));
        idcol.setCellValueFactory(new PropertyValueFactory<>("id"));
        authorcol.setCellValueFactory(new PropertyValueFactory<>("author"));
        pubcol.setCellValueFactory(new PropertyValueFactory<>("publisher"));
    }
         @FXML
        public void search(KeyEvent event) throws SQLException{
       searchId.textProperty().addListener((observable,oldValue,newValue) ->{
         /*  try {
               String id = newValue;
               sql_books = "SELECT * FROM tbl_addbook WHERE title LIKE '%"+ id + "%' OR author LIKE '%"+ id +"%' OR publisher LIKE '%"+ id +"%'";
               Connection conn = ConnectDB.getConnections();
               PreparedStatement pst1 = conn.prepareStatement(sql_books);
               ResultSet rst = pst1.executeQuery();
               startBook();
           } catch (SQLException ex) {
               Logger.getLogger(searchWindowController.class.getName()).log(Level.SEVERE, null, ex);
           }
*/
         filterdata.setPredicate(Book -> {
         
             if (newValue == null || newValue.isEmpty())
             {
                 return true;
             }
             String lower = newValue.toLowerCase();
             if (String.valueOf(Book.getTitle()).toLowerCase().contains(lower))
             {
                 return true;
             }
             else if(String.valueOf(Book.getPublisher()).toLowerCase().contains(lower))
             {
                 return true;
             }
              else if(String.valueOf(Book.getAuthor()).toLowerCase().contains(lower))
             {
                 return true;
             }
             return false;
             
             
         
         
         
         });
         
       
       });
          titlecol.setCellValueFactory(new PropertyValueFactory<>("title"));
        idcol.setCellValueFactory(new PropertyValueFactory<>("id"));
        authorcol.setCellValueFactory(new PropertyValueFactory<>("author"));
        pubcol.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        
        SortedList<Book> sortdata = new SortedList<>(filterdata);
        
        sortdata.comparatorProperty().bind(tableview.comparatorProperty());
        
        tableview.setItems(sortdata);
           
        }
    public void LoadData(){
        String sql = "SELECT * FROM tbl_addbook";
        try {
            Connection conn = ConnectDB.getConnections();
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            
            while(rs.next()){
                String title = rs.getString("title");
                String id = rs.getString("id");
                String author = rs.getString("author");
                String publisher = rs.getString("publisher");
                
                list.add(new Book(title,id,author,publisher));
            }
        } catch (SQLException ex) {
            Logger.getLogger(BookListController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        tableview.getItems().setAll(list);
    }
    
    public static class Book{
            
        private final SimpleStringProperty title;
        private final SimpleStringProperty id;
        private final SimpleStringProperty author;
        private final SimpleStringProperty publisher;
            
        public Book(String title,String id, String author,String pub){
            this.title = new SimpleStringProperty(title);
            this.id = new SimpleStringProperty(id);
            this.author = new SimpleStringProperty(author);
            this.publisher = new SimpleStringProperty(pub);
        }
            
            public String getTitle() {
                return title.get();
            }

       
            public String getId() {
                return id.get();
            }

        
            public String getAuthor() {
                return author.get();
            }

        
            public String getPublisher() {
                return publisher.get();
            }
        }
       
    
}
