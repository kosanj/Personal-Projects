
#Imports, notice * wildcard meaning use tkinter.ttk methods were possible, else revert to tkinter classic methods
from tkinter import *
from tkinter.ttk import *
#Manual import as old version
import tkinter as tk
import tkinter.ttk as ttk
#Selenium Imports
from selenium import webdriver
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.action_chains import ActionChains
from selenium.webdriver.chrome.options import Options
import time
from PIL import ImageTk, Image
from selenium.common.exceptions import NoSuchElementException
import sys
import os
from webdriver_manager.chrome import ChromeDriverManager

#Global Variables
term_row = None
back_status = False
userid = None
userpwd = None

window = tk.Tk() #open a window
window.geometry("1250x1000")

#Function: convert grade from letter to 12-pt scale
def calculate_grade(grade):
    if grade == "F": return "0"
    elif grade == "D-": return "1"
    elif grade == "D": return "2"
    elif grade == "D+": return "3"
    elif grade == "C-": return "4"
    elif grade == "C": return "5"
    elif grade == "C+": return "6"
    elif grade == "B-": return "7"
    elif grade == "B": return "8"
    elif grade == "B+": return "9"
    elif grade == "A-": return "10"
    elif grade == "A": return "11"
    elif grade == "A+": return "12"
    else: return "N/A"

#Function: select a colour for the progress bar based on the amount of courses
def select_colour(index):
    index = index-6 #correction

    if index == 0: return "#2ABE50"
    elif index == 1: return "#82E2D6"
    elif index == 2: return "#D36C96"
    elif index == 3: return "#AEE59B"
    elif index == 4: return "#67B2E3"
    else : return "green"

#Function: generate a circular progress bar
def generate_progress_bar (index, grade, subject, content_frame):
    frame = tk.Frame(master=content_frame)
    frame.grid(row=1, column=index-6)  # Adjust padx and pady as needed

    # Create a separate frame for the circular progress bar
    progress_frame = tk.Frame(master=frame)
    progress_frame.grid(row=0, column=0)  # Adjust padx and pady as needed

    # Draw a circle on the progress frame
    radius = 50
    canvas = tk.Canvas(master=progress_frame, width=2 * radius + 14, height=2 * radius + 14) #+14 is a shift to make the canvas big enough
    canvas.grid(row=1, column=0)

    # Draw the background circle
    canvas.create_oval(4, 4, 2 * radius + 4, 2 * radius + 4, outline="#7E7B7B", fill="#7E7B7B") #4s and +4s are a (4,4) shift to make sides of circle fit

    # Calculate the extent based on the grade
    grade_number = calculate_grade(grade)
    if grade_number == "N/A":
        extent_val = 0
    else:
        extent_val = -int(grade_number)* 30

    # Draw the progress arc
    arc_rad = 48
    if grade_number != "12":
        canvas.create_arc(radius - arc_rad + 4, radius - arc_rad + 4, radius + arc_rad + 4, radius + arc_rad + 4, start=90, extent=extent_val, outline="#5C5C5C", fill=select_colour(index), width=1)
        canvas.create_arc(radius - arc_rad + 4, radius - arc_rad + 4, radius + arc_rad + 4, radius + arc_rad + 4, start=90, extent=360+extent_val, outline="#5C5C5C", fill="#D3D3D3", width=1)
    else:
        canvas.create_oval(radius - arc_rad + 4, radius - arc_rad + 4, radius + arc_rad + 4, radius + arc_rad + 4, outline="#5C5C5C", fill=select_colour(index), width=1)

    #Add the inner circle
    inner_radius = 35
    canvas.create_oval(radius - inner_radius + 4, radius - inner_radius + 4, radius + inner_radius + 4, radius + inner_radius + 4, outline="#5C5C5C", fill="#7E7B7B", width=1)

    # Add the grade text
    radius = 50
    canvas.create_text(radius + 4, radius + 4, text=grade_number, fill="white", font=("Arial", 16, "bold"), justify="center")

    # Add the subject label
    label = tk.Label(master=frame, text=subject)
    label.grid(row=2, column=0)  # column=0 because this label is in the new frame which is already positioned at the right spot

#Function: Destroy all widgets within the window
def clear_window(window):
    for widget in window.winfo_children(): #window.winfo_children() is a method to get all the widgets window
        widget.destroy()

#Function: set term_row variable for selection of term
def set_term_row(row):
    global term_row
    term_row = row
    window.quit() #this means terminate window.mainloop() waiting command

#Function: loop back to option display screen
def set_back_condition():
    global back_status
    back_status = True
    window.quit()

#Function: Update the progress bar when the output page is loading still
def update_progress_bar(prog_bar):
    prog_bar['value'] += 30
    window.after(50, window.update())

#Function: Check if an element exists
def check_exists_by_id(driver,id_elem):
    try:
        driver.find_element_by_id(id_elem)
    except NoSuchElementException:
        return False
    return True

#Function: set login details so we can try to login
def set_login_details(entry1, entry2):
    global userid
    global userpwd
    userid = entry1.get()
    userpwd = entry2.get()
    window.quit()
    

#Main function: scrape mosaic for info then print into GUI
def main():
    
    #SELENIUM: Set up driver and open URL
    #driver_path = '/Users/anjalikosuri/Documents/Coding Projects/chromedriver_mac_arm64/chromedriver'   #driver path
    driver_path = ChromeDriverManager().install() #driver path - using an driver manager library instead so chrome driver is always compatible with the version of chrome we have
    s=Service(driver_path)
    options = Options();                                                                #driver options - headless means chrome will run without opening 
    options.add_argument("--headless=new");
    driver = webdriver.Chrome(service=s,options=options)
    url = 'https://epprd.mcmaster.ca/psp/prepprd/?cmd=login'                            #open URL
    driver.get(url)

    #SELENIUM + GUI: Enter Password
    error_exists = False
    global userid
    global userpwd

    if(userid == None and userpwd == None): #if no username or password has been successfully set yet
        while True:
            
            pwd_frame = tk.Frame(master=window, width = 500, height = 50, highlightthickness=2, highlightbackground="black") 
            pwd_frame.pack(pady=100, ipadx = 100)
            
            tk.Label(master=pwd_frame,text="MacID").pack(pady=10)   #MacID
            entry1=tk.Entry(master=pwd_frame)
            entry1.pack(pady=10)
            
            tk.Label(master=pwd_frame,text="Password").pack(pady=10) #Password
            entry2=tk.Entry(master=pwd_frame,show="●")
            entry2.pack(pady=10)
            window.update()
            
            if error_exists: #incorrect details message
                tk.Label(master=pwd_frame,text="Incorrect MacID and/or password", font = ("TkDefaultFont", 16), fg="red").pack()

            entry_button = tk.Button(master=pwd_frame,text="Enter",cursor="hand2",command=lambda: set_login_details(entry1, entry2),font = ("TkDefaultFont", 16, "bold underline"))
            entry_button.pack(pady=20)

            window.mainloop()   #Wait for user input

            #SELENIUM: Enter login details and submit
            driver.find_element_by_name("userid").send_keys(userid)
            driver.find_element_by_name("pwd").send_keys(userpwd)
            nxt = driver.find_element_by_name("Submit")
            nxt.click()
            
            if check_exists_by_id(driver,"login_error"): #If error message popped up on mosaic screen, then incorrect login details
                error_exists = True
                userid = None
                userpwd = None
                clear_window(window)#clear window so we can display terms options
            else: #if successful login, contiue to next part
                clear_window(window)#clear window so we can display terms options
                break
            
    else: #if we already have login details (now checking another course), then no need to enter again
        #SELENIUM: Enter login details and submit
        driver.find_element_by_name("userid").send_keys(userid)
        driver.find_element_by_name("pwd").send_keys(userpwd)
        nxt = driver.find_element_by_name("Submit")
        nxt.click()

    #GUI: Loading Splash Screen
    image_frame = tk.Frame(master=window, width=500, height=50)
    image_frame.pack(pady=100)
    base_dir = getattr(sys, '_MEIPASS', os.path.dirname(os.path.abspath(__file__)))
    image_path = os.path.join(base_dir, "McMaster_Logo.png")
    resize_img = Image.open(image_path).resize((300, 200))
    img = ImageTk.PhotoImage(resize_img)
    img_label = tk.Label(image_frame, image=img)
    img_label.pack()
    text_frame = ttk.Frame(master=window, width=500, height=50)
    text_frame.pack()
    loading_label = tk.Label(master=text_frame, text='Loading...')
    loading_label.pack()
    loading_frame = ttk.Frame(master=window, width=500, height=50)
    loading_frame.pack()
    prog_bar = ttk.Progressbar(master=loading_frame, orient='horizontal', length=280, mode='determinate')
    prog_bar.pack()
    window.after(500, window.update())
    prog_bar.start()
    update_progress_bar(prog_bar)

    #SELENIUM: Click Grades
    nxxt = driver.find_element_by_xpath("/html/body/form/div[2]/div[4]/div[2]/div/div/div/div/div[3]/section/div/div[2]/div/div[1]/div[2]/div/div[2]/div/div/div/div[9]/div[1]/div")
    nxxt.click()
    update_progress_bar(prog_bar)

    #SELENIUM: Sometimes it auto-opens to the most recent term rather than term selection page. Hit enter to close the alert message then press the button to go to the term selection page
    actions = ActionChains(driver)
    time.sleep(.5)
    actions.send_keys(Keys.RETURN)  # Simulate pressing the return key
    actions.perform()
    try:
        iframe = driver.find_element_by_xpath("/html/body/div[4]/div[1]/iframe")
        driver.switch_to.frame(iframe)
        changeTerm = driver.find_element_by_name("DERIVED_SSS_SCT_SSS_TERM_LINK")
        changeTerm.click()
    except:
        pass

    #SELENIUM: Switch to frame of table
    driver.switch_to.default_content()
    time.sleep(0.5)
    iframe = driver.find_element_by_xpath("/html/body/div[4]/div[1]/iframe")
    driver.switch_to.frame(iframe)
    update_progress_bar(prog_bar)

    #GUI: clear window for next window output
    prog_bar.stop()
    clear_window(window)#clear window so we can display terms options
    
    #GUI: window has options for terms
    second_column = driver.find_elements_by_xpath("//table[@class='PSLEVEL2GRIDWBO']//tr/td[2]") #get data
    column_data = [cell.text for cell in second_column]
    print(column_data)
    
    option_frame = tk.Frame(master=window, width=100, height = 20)
    option_frame.pack(pady=100, ipadx = 100, ipady = 50)
    for i,column in enumerate(column_data):              #printing loop
        term_button = tk.Button(master=option_frame,text=column,cursor="hand2",width=25,anchor="w",justify="left",command=lambda row=i: set_term_row(row))
        term_button.pack()
        
    window.mainloop() #wait for user interaction (press an option)
                                                
    #SELENIUM: Select RadioButton and submit
    radio_buttons = driver.find_elements_by_xpath("//table[@class='PSLEVEL2GRIDWBO']" + "//input[@type='radio']")
    rd_id = radio_buttons[term_row].get_attribute("id")
    rdbutton = driver.find_element_by_id(rd_id)
    rdbutton.click()
    cont = driver.find_element_by_name("DERIVED_SSS_SCT_SSR_PB_GO")
    cont.click()

    driver.switch_to.default_content()

    #SELENIUM: Hit enter to close the alert message
    actions = ActionChains(driver)
    time.sleep(.5)
    actions.send_keys(Keys.RETURN)  # Simulate pressing the return key
    actions.perform()

    #SELENIUM: Switch to frame of table
    iframe = driver.find_element_by_xpath("/html/body/div[4]/div[1]/iframe")
    driver.switch_to.frame(iframe)

    ###TABLE###

    time.sleep(.5)
    table = driver.find_element_by_xpath("//table[@class='PSLEVEL1GRID']") # //table is all <table> elements and [@class] specification to specify which table by using the table id label

    clear_window(window)#clear window so we can display grades output

    canvas = tk.Canvas(master = window)
    canvas.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)
    scrollbar = ttk.Scrollbar(master = window, orient=tk.VERTICAL, command=canvas.yview)
    scrollbar.pack(side=tk.RIGHT, fill=tk.Y)
    canvas.configure(yscrollcommand=scrollbar.set)
    canvas.bind("<Configure>", lambda e: canvas.configure(scrollregion=canvas.bbox("all")))
    content_frame = ttk.Frame(master = canvas)
    canvas.create_window((0, 0), window = content_frame, anchor=tk.NW)
    
    #GUI: blank line
    blank_frame = tk.Frame(master=content_frame, width=100, height=20) 
    blank_frame.grid(row=3, column=0, columnspan=6, pady=20) #column span = 6 is to span across 6 columns, pady is to create space as height 20
    
    #GUI: Add a subtitle
    heading_prog = tk.Frame(master=content_frame, width=100, height=20)
    heading_prog.grid(row=0, column=0, columnspan = 6)
    tk.Label(master=heading_prog, text = "------ Grades ------", font = ("TkDefaultFont", 20, "bold underline"), pady = 10).pack() 
    
    #GUI: Add a subtitle
    heading_fullinfo = tk.Frame(master=content_frame, width=100, height = 20)
    heading_fullinfo.grid(row=4, column=0, columnspan = 6)
    tk.Label(master=heading_fullinfo, text = "------ Detailed Breakdown ------", font = ("TkDefaultFont", 20, "bold underline"), pady = 10).pack()
    
    # Iterate over each row and extract the data
    header_labels = ["Course Code", "Course Name", "Units", "Status", "Letter Grade", "Grade Points"]
    for i in range(6):
        frame = tk.Frame(master=content_frame)
        tk.Label(master=frame, text=header_labels[i], width=16, height=2, font=("TkDefaultFont", 16, "bold underline")).pack()
        frame.grid(row=5,column=i)

    #SELENIUM + GUI: Get all rows in the table (excluding the header row)
    rows = table.find_elements_by_xpath(".//tr[position() > 1]") #selecting all rows tr except for the first(header) row

    row_n = 6
    col_n = 0
    for row in rows:
                            
        cells = row.find_elements_by_xpath(".//td") #select all columns td
        row_data = [cell.text for cell in cells] #create an array of the data in each row by taking the text using .text in each column
        ##print(row_data)
        
        if row_data[2].strip() == "": continue #if a course has units empty, it means it is either a training or multiterm course, so just skip it

        generate_progress_bar(row_n, row_data[4], row_data[0], content_frame) #generate progress bar for each subject
        
        for column in row_data:
            frame = tk.Frame(master=content_frame, relief=tk.RAISED, borderwidth=2)
            tk.Label(master=frame, text=column, width = 20, height = 5).pack()
            frame.grid(row=row_n,column=col_n)
            col_n+=1

        col_n = 0
        row_n+=1


    #Close tab and disable driver
    driver.close()
    driver.quit()
    
    #GUI: Add a back button
    back_frame = tk.Frame(master=content_frame)
    back_frame.grid(row=row_n, column=0, columnspan = 6)
    back_button = tk.Button(master=back_frame,text="Back ↶ ",cursor="hand2",anchor="e",justify="right",command=lambda: set_back_condition(),font = ("TkDefaultFont", 20, "bold underline"))
    back_button.pack(pady=20)
    
    window.update_idletasks()   #must resize the scrollbar to be active for the region of the canvas after all the widgets have been added
    canvas.configure(scrollregion=canvas.bbox("all"))
    
    window.mainloop() #Wait for user to press back button

    global back_status
    if back_status == True:
        clear_window(window)
        back_status = False
        main()

main()  #call main

window.mainloop() #Always add at end of program to wait for user to press X to close the window, before the shell can be interacted with again
