package se.nackademin.librarytest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


import org.junit.Test;
import se.nackademin.selenide.helpers.AuthorHelper;
import se.nackademin.selenide.helpers.BookHelper;
import se.nackademin.selenide.helpers.UserHelper;
import se.nackademin.selenide.model.User;
import se.nackademin.selenide.pages.BookViewPage;
import se.nackademin.selenide.pages.MenuPage;
import se.nackademin.selenide.pages.MyProfilePage;
import static com.codeborne.selenide.Selenide.sleep;
import static org.hamcrest.CoreMatchers.containsString;
import se.nackademin.selenide.pages.UserFormPage;
import static se.nackademin.librarytest.Constants.LIBRARIAN_USER;
import static se.nackademin.librarytest.Constants.LIBRARIAN_PWD;
import static se.nackademin.librarytest.Constants.LOANER_PWD;
import static se.nackademin.librarytest.Constants.LOANER_USER;
import se.nackademin.selenide.pages.AuthorViewPage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;
import static com.codeborne.selenide.Selenide.page;

public class SelenideTest extends SelenideTestBase {

    public SelenideTest() {
    }
    
    /*
    @Test
    public void testUsingTable() {
        page(MenuPage.class).navigateToBrowseBooks();
        BrowseBooksPage browseBooksPage = page(BrowseBooksPage.class);
        //browseBooksPage.setTitleField("G");
        browseBooksPage.clickSearchBooksButton();
        Table table = new Table($(".v-grid-tablewrapper"));
        System.out.println(table.getColumnCount());
        System.out.println(table.getRowCount());
        System.out.println(table.getCellValue(0, 0));
        System.out.println(table.getCellValue(1, 1));
        table.searchAndClick("American Gods", 0);
        sleep(2000);
    }*/
    
    
    /**
     * Skapa en ny författare
     */
    @Test
    public void testCreateModifyDeleteAuthor(){

        String firstName = UUID.randomUUID().toString();
        String lastName = "X";
        String shortBiography = "Short bio";
        String longBiography = "Long biography bla bla bla...";
        String country = "USA";

        // Log in as admin user and create new author
        UserHelper.logInAsUser(LIBRARIAN_USER, LIBRARIAN_PWD);        
        AuthorHelper.createAuthor(firstName, lastName, country, shortBiography);

        // Verify fetched author
        AuthorViewPage authorViewPage = AuthorHelper.fetchAuthor(firstName);
        assertEquals("Authors name should be first and last names concatenated", firstName +" "+ lastName, authorViewPage.getName());
        assertEquals(country, authorViewPage.getCountry());
        assertEquals(shortBiography, authorViewPage.getBiography());      
        
        authorViewPage.clickEditAuthorButton();

        AuthorHelper.editAuthor(firstName, lastName, country, longBiography);
        // Verify fetched author
        authorViewPage = AuthorHelper.fetchAuthor(firstName);
        assertEquals("Authors name should be first and last names concatenated", firstName +" "+ lastName, authorViewPage.getName());
        assertEquals(country, authorViewPage.getCountry());
        assertEquals(longBiography, authorViewPage.getBiography());  

        authorViewPage.clickEditAuthorButton();        
        
    }

    /**
     * Skapa användare, logga in - logga ut
     */
    @Test
    public void testSignInAndSignOut(){
        
        // Sign in
        UserHelper.logInAsUser(LIBRARIAN_USER, LIBRARIAN_PWD);
        
        // Verify there is a link to the profile page
        MenuPage menuPage = page(MenuPage.class); 
        
        menuPage.waitForProfileLinkToShow();
        
        // For some reason, we must wait a while
        sleep(1000);
        assertTrue("Link to profile page is not visible",menuPage.isMyProfileLinkDisplayed());

        // Sign out        
        menuPage.clickSignOut();
        // Verify there is NO link to the profile page        
        assertFalse("Link to profile page should not be visible",menuPage.isMyProfileLinkDisplayed());
        
      
    }    
    
    /**
     * Ändra e-mailadress
     */
    @Test
    public void testCreateUserAndUpdateEmail(){

        final String uuid = UUID.randomUUID().toString();
        final String oldEmail = "abc.def@mailserver.com";
        final String newEmail = "fornman.efternamn@mailserver.com";
        User user;
        
        // Create user
        UserHelper.createNewUser(uuid, uuid, "firstname", "lastname", "010 - 12345678", oldEmail, false);
        
        // Verify email is correctly set
        user = UserHelper.fetchUser(uuid, uuid);        
        assertEquals(oldEmail, user.getEmail());
        
        // Update user's email
        UserHelper.updateLoggedInUser(null, null, null, null, newEmail);

        // Verify email has changed
        user = UserHelper.fetchUser(uuid, uuid);
        assertEquals(newEmail, user.getEmail());        
    }


    /**
     * Skapa ny bok
     */
    @Test
    public void testCreateAndDeleteBook(){
        
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        final String datePublished = df.format(new Date());
        final String title = UUID.randomUUID().toString();        
        final String description = "Lorem ipsum dolor sit amet, sit te tation putent.";  
        final String expectedAuthor = "Terry Pratchett";
        
        // Log in as admin user
        UserHelper.logInAsUser(LIBRARIAN_USER, LIBRARIAN_PWD);
        
        // Crate a nw book
        BookHelper.createBook(title, description, null, null, "1", datePublished); 

        BookViewPage bookViewPage = BookHelper.fetchBookPage(title);
        
        assertEquals(title, bookViewPage.getTitle());
        assertEquals(description, bookViewPage.getDescription());   
        assertEquals(expectedAuthor, bookViewPage.getAuthor());
        
        bookViewPage.clickDeleteBookButton();
        bookViewPage.clickConfirmDialogOKButton();
        
    }
    
    
    /**
     * Ändra publiceringsdatum
     */
    @Test
    public void testUpdateBookDatePublished(){
        
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        final String todaysDate = df.format(new Date());
        final String originalDatePublished = "1990-05-01";
        final String bookTitle = "Good Omens";         
        
        // Log in as admin user
        UserHelper.logInAsUser(LIBRARIAN_USER, LIBRARIAN_PWD);
        
        // We dont know for sure what "date published" the book has
        // Therefore, we must change the value twice
        BookHelper.editBook(bookTitle, null, null, null, null, null, todaysDate);         
        assertEquals(todaysDate, BookHelper.fetchBookPage(bookTitle).getDatePublished());

        BookHelper.editBook(bookTitle, null, null, null, null, null, originalDatePublished);
        assertEquals(originalDatePublished, BookHelper.fetchBookPage(bookTitle).getDatePublished());
    }
    
    /**
    * Skapa en ny bok
    * Låna den nya boken (som LIBRARIAN)
    * Logga in som LOANER och kolla att det inte går att låna boken
    * Logga in med samma användare som lånade boken och lämna tillbaka den
    */
    @Test
    public void testCreateUserAndBorrowBook(){

        // Log in with LIBRARIAN privileges
        UserHelper.logInAsUser(LIBRARIAN_USER, LIBRARIAN_PWD);

        final String bookTitle = UUID.randomUUID().toString();

        // Crate a nw book (Nbr of books in inventory = 1)
        final int nbrInInventory = 1;
        BookHelper.createBook(bookTitle, "Description", null, null, String.valueOf(nbrInInventory), "2016-06-03"); 
        
        // Borrow book
        BookHelper.borrowBook(bookTitle);

        // Verify that the number of available books is decremeted by one
        sleep(2000);
        BookViewPage bookPage = BookHelper.fetchBookPage(bookTitle);        
        int nbrOfCopiesAvailable = Integer.parseInt(bookPage.getNbrOfCopiesAvailable()); 
        // For some reason, we need to wait a moment
        // sleep(1000);
        assertEquals("Number of copies availabe is not decremented", nbrInInventory - 1, nbrOfCopiesAvailable);
        
        // View the same book, from a different user
        UserHelper.logInAsUser(LOANER_USER, LOANER_PWD);
        
        bookPage = BookHelper.fetchBookPage(bookTitle);        
        // Verify that there are no available copies of the book and there is no "Borrow Book" button
        assertEquals("Number of copies available should be zero", 0, Integer.parseInt(bookPage.getNbrOfCopiesAvailable()));
        assertFalse("Borrow Book-button should not be visible", bookPage.isBorrowBookButtonDisplayed());
        
        // Log in as admin again
        UserHelper.logInAsUser(LIBRARIAN_USER, LIBRARIAN_PWD);        
        
        // Return book
        BookHelper.returnBook(bookTitle);
        
        // Verify that the number of available boos is restored to original value
        // For some reason, we need to wait a moment        
        // sleep(1000);
        bookPage = BookHelper.fetchBookPage(bookTitle); 
        assertEquals("Number of copies availabe is wrong", nbrInInventory, Integer.parseInt(bookPage.getNbrOfCopiesAvailable()));
        
    }
    
    /**
    * Skapa librarian-användare
    */
    @Test
    public void testCreateLibrarianUser(){

        // Log in as admin user
        UserHelper.logInAsUser(LIBRARIAN_USER, LIBRARIAN_PWD);
        final String uuid = UUID.randomUUID().toString();
        UserHelper.createNewUser(uuid, uuid, "firstname", "lastname", "010 - 12345678", "admin@server.com", true);
        UserHelper.logInAsUser(uuid, uuid);
        
        MenuPage menuPage = page(MenuPage.class);
        menuPage.navigateToMyProfile();    
        
        MyProfilePage myProfilePage = page(MyProfilePage.class);
        myProfilePage.clickDeleteUserButton();
        myProfilePage.clickConfirmDeleteUserOkButton();
        
    }
    
    /**
    * Skapa användare med ett redan upptaget "Display Name"
    */
    @Test
    public void testCreateUserWithDisplayNameAlreadyTaken(){

        // Try to create user with display name "admin"
        UserFormPage page = UserHelper.createNewUser(LIBRARIAN_USER, LIBRARIAN_PWD, "firstname", "lastname", "010 - 12345678", "admin@server.com", false);
        assertThat(page.getMessage(), containsString("display name already exists"));
        
    }
    
    @Test
    public void verifyLinksAndButtonsForLoaner(){
        
        String uuid = UUID.randomUUID().toString();

        // Log in with role "LOANER"        
        UserHelper.logInAsUser(LOANER_USER, LOANER_PWD);
        
        MenuPage menuPage = page(MenuPage.class);
        sleep(2000);
        assertFalse("Add Book link should not be visible for LOANER", menuPage.isAddBookLinkDisplayed());
        assertFalse("Add Author link should not be visible for LOANER", menuPage.isAddAuthorLinkDisplayed());        
        assertFalse("Add User link should not be visible for LOANER", menuPage.isAddUserLinkDisplayed()); 
        assertTrue("My Profile link should be visible for LOANER", menuPage.isMyProfileLinkDisplayed());

        menuPage.navigateToMyProfile();
        MyProfilePage myProfilePage = page(MyProfilePage.class);
        sleep(2000);        
        assertFalse("Delete User-button should not be visible for LOANER", myProfilePage.isDeleteUserButtonDisplayed());
        
        // Här skulle man även kunna kolla att det inte finns några knappar för Edit / Delete under View Boook / Author
        // Testfallet blir då lite väl stort.
    }
    
    @Test
    public void verifyMenuLinksAndButtonsForLibrarian(){
        
        // Log in with role "LIBRARIAN"
        UserHelper.logInAsUser(LIBRARIAN_USER, LIBRARIAN_PWD);
        
        MenuPage menuPage = page(MenuPage.class);
        sleep(2000);
        assertTrue("Add Book link should be visible for LIBRARIAN", menuPage.isAddBookLinkDisplayed());
        assertTrue("Add Author link should be visible for LIBRARIAN", menuPage.isAddAuthorLinkDisplayed());        
        assertTrue("Add User link should be visible for LIBRARIAN", menuPage.isAddUserLinkDisplayed()); 
        assertTrue("My Profile link should be visible for LIBRARIAN", menuPage.isMyProfileLinkDisplayed()); 

        menuPage.navigateToMyProfile();
        MyProfilePage myProfilePage = page(MyProfilePage.class);
        sleep(2000);
        assertTrue("Delete User-button should be visible for LIBRARIAN", myProfilePage.isDeleteUserButtonDisplayed());
        
        // Här skulle man även kunna kolla att det finns knappar för Edit / Delete under View Boook / Author
        // Sådana "djupa" tester hör dock bättre hemma i testerna av create Book / Author längre upp i testsviten        
    }    
    
}
