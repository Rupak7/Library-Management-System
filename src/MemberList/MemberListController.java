
package MemberList;

import DButils.ConnectDB;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
//author Nitish Kumar Singh
//main controller
public class MemberListController implements Initializable {

    @FXML
    private TableView<Member> tableview;
    @FXML
    private TableColumn<Member, String> namecol;
    @FXML
    private TableColumn<Member, String> idcol;
    @FXML
    private TableColumn<Member, String> numcol;
    @FXML
    private TableColumn<Member, String> emailcol;
 @FXML
    private TextField memberId;

   
    private ConnectDB connectDB;
    ObservableList<Member> list= FXCollections.observableArrayList();
   FilteredList<Member> filterdata = new FilteredList<>(list,e->true);

    @Override
    //database connection
    public void initialize(URL url, ResourceBundle rb) {
        initcol();
        connectDB = new ConnectDB();
        LoadData();

        tableview.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }
// delet button action
    public void deleteButtonpushed()
    {
        ObservableList<Member> selectedRows,allMembers;
        allMembers = tableview.getItems();

        selectedRows = tableview.getSelectionModel().getSelectedItems();

        for(Member member:selectedRows)
        {
            allMembers.remove(member);
        }
    }

    public void initcol(){
        namecol.setCellValueFactory(new PropertyValueFactory<>("name"));
        idcol.setCellValueFactory(new PropertyValueFactory<>("id"));
        numcol.setCellValueFactory(new PropertyValueFactory<>("mobile"));
        emailcol.setCellValueFactory(new PropertyValueFactory<>("email"));
    }
    //loading data
    public void LoadData(){
        String sql = "SELECT * FROM tbl_addmember";
        try {
            Connection conn = ConnectDB.getConnections();
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            while(rs.next()){
                String name = rs.getString("name");
                String id = rs.getString("id");
                String mobile = rs.getString("mobile");
                String email = rs.getString("email");

                list.add(new Member(name,id,mobile,email));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MemberListController.class.getName()).log(Level.SEVERE, null, ex);
        }

        tableview.getItems().setAll(list);
    }
      @FXML
        public void member(KeyEvent event) throws SQLException{
       memberId.textProperty().addListener((observable,oldValue,newValue) ->{
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
         filterdata.setPredicate(member -> {
         
             if (newValue == null || newValue.isEmpty())
             {
                 return true;
             }
             String lower = newValue.toLowerCase();
             if (String.valueOf(member.getEmail()).toLowerCase().contains(lower))
             {
                 return true;
             }
             else if(String.valueOf(member.getId()).toLowerCase().contains(lower))
             {
                 return true;
             }
              else if(String.valueOf(member.getMobile()).toLowerCase().contains(lower))
             {
                 return true;
             }
             else if(String.valueOf(member.getName()).toLowerCase().contains(lower))
             {
                 return true;
             }
             return false;
             
             
         
         
         
         });
         
       
       });
            namecol.setCellValueFactory(new PropertyValueFactory<>("name"));
        idcol.setCellValueFactory(new PropertyValueFactory<>("id"));
        numcol.setCellValueFactory(new PropertyValueFactory<>("mobile"));
        emailcol.setCellValueFactory(new PropertyValueFactory<>("email"));
        
        SortedList<Member> sortdata = new SortedList<>(filterdata);
        
        sortdata.comparatorProperty().bind(tableview.comparatorProperty());
        
        tableview.setItems(sortdata);
           
        }
    //member function
    public static class Member{

        private final SimpleStringProperty name;
        private final SimpleStringProperty id;
        private final SimpleStringProperty mobile;
        private final SimpleStringProperty email;

        public Member(String name,String id, String mobile,String email){
            this.name = new SimpleStringProperty(name);
            this.id = new SimpleStringProperty(id);
            this.mobile = new SimpleStringProperty(mobile);
            this.email = new SimpleStringProperty(email);
        }

        public String getName() {
            return name.get();
        }


        public String getId() {
            return id.get();
        }


        public String getMobile() {
            return mobile.get();
        }


        public String getEmail() {
            return email.get();
        }

    }


}
