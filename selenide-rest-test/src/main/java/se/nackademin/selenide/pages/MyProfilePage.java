/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nackademin.selenide.pages;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;

/**
 * @author testautomatisering
 */
public class MyProfilePage extends MenuPage {
    @FindBy(css = "#gwt-uid-5")
    private SelenideElement userNameField;

    @FindBy(css = "#gwt-uid-7")
    private SelenideElement firstNameField;
    
    @FindBy(css = "#gwt-uid-9")
    private SelenideElement lastNameField;

    @FindBy(css = "#gwt-uid-11")
    private SelenideElement phoneNbrField;    
    
    @FindBy(css = "#gwt-uid-13")
    private SelenideElement emailField;        
    
    @FindBy(css = "#edit-user")
    private SelenideElement editUserButton;
    
    // Detete user button only visible for users of type "LIBRARIAN"
    @FindBy(css = "#delete-user-button")
    private SelenideElement deleteUserButton;
    
    @FindBy(css = "#confirmdialog-ok-button")
    private SelenideElement confirmDeleteUserButton;   

    @FindBy(css = "td.v-grid-cell:nth-child(1) > a:nth-child(1)")
    private SelenideElement firstResultTitle;
    

    public String getUserName() {
        return userNameField.getText();
    }

    public String getFirstName() {
        return firstNameField.getText();
    }

    public String getLastName() {
        return lastNameField.getText();
    }    
    
    public String getPhoneNbr() {
        return phoneNbrField.getText();
    }
    
    public String getEmail() {
        return emailField.getText();
    }
    
    public void clickEditUserButton() {
        editUserButton.click();
    }
    
    public void clickDeleteUserButton() {
        deleteUserButton.click();
    }        
    
    public void clickConfirmDeleteUserOkButton() {
        confirmDeleteUserButton.click();
    } 
    
    public void clickFirstResultTitle() {
        firstResultTitle.click();
    }
    
     /**
     * Checks whether deleteUserButton is visible or not
     * @return flag signaling the visibility of "Delete User" button
     */
    public boolean isDeleteUserButtonDisplayed() {
        return deleteUserButton.isDisplayed();
    }  
}
