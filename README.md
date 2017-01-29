This project uses starter code from https://github.com/cybersecuritybase/cybersecuritybase-project. The project is simple web application which offers the ability to sign up for a event. It also offers possibility to login as "admin" and view/delete participants. The application has five different flaws from the OWASP top ten list which are presented below.

### Things to do before testing:
- Download and install Owasp ZAP (https://github.com/zaproxy/zaproxy/wiki/Downloads)
- Download and install Git client (https://git-scm.com/download)
- Open Owasp ZAP and go to "Tools" -> "Options" -> "Local Proxy" -> change the "Port" to 8081 -> click "OK" -> close Owasp ZAP
- Clone the git repository (git clone https://github.com/terotuomala/cybersecuritybase-project.git)
- Open the project in IDE (e.g. NetBeans)
- Run the project from IDE or
- Open command line tool (e.g. PowerShell in Windows) and go to project folder
- Run command `mvn spring-boot:run`
- Go to page http://localhost:8080 in your browser

-------------------------------------------------------------------------

### Issue 1: A9-Using Components with Known Vulnerabilities
**Steps to reproduce:**

1. Open the project folder in command line tool (e.g. PowerShell in Windows)
2. Run command `mvn dependency-check:check`
3. Go through the list of vulnerable dependencies
4. Find out their description using the Common Vulnerabilities and Exposures database at https://cve.mitre.org/cve/cve.html

**Solution:**
- Identify all vulnerable dependencies and the versions of them
- If possible update them to newer versions
- Open `"pom.xml"` in a editor of your choice
- Edit "org.springframework.boot" to use latest version (1.4.3.RELEASE)
```java
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>1.4.3.RELEASE</version>
    </parent>
```
- Open command line tool (e.g. PowerShell in Windows) and go to project folder
- Run command `mvn dependency:resolve`


### Issue 2: A6-Sensitive Data Exposure
**Steps to reproduce:**

1. Download password list from https://github.com/danielmiessler/SecLists/blob/master/Passwords/10k_most_common.txt
2. Open Owasp ZAP
3. Configure your browser to use Owasp ZAP as proxy (in Firefox you can use e.g. FoxyProxy)
4. Go to URL http://localhost:8080/login
5. Enter *"admin"* as username and password of your choice and press *"Login"*
6. In Owasp ZAP extend *"Sites"* (on the left), extend "http://localhost:8080"
7. Right click *"POST:login(password,submit,username)* and choose *"Attack" -> "Fuzz.."*
8. Hightlight the value from password field and click *"Add" -> click "Add" -> choose "File" as "Type" -> Click "Select" -> browse the location of 10k_most_common.txt file -> click "Open" -> click "Add" -> click "OK" -> click "Start Fuzzer"*
9. Open the *"Fuzzer"* tab and sort the results by starting from smallest *"Size Resp. Header"*
10. Inspect the results
11. Take a wild guess what the right password is and try it out :)

**Solution:**
- Use more secure password (e.g. https://xkcd.com/936/)
- Use encrypted HTTPS (SSL/TLS) connection for all pages
- You can enable HTTPS in embedded Tomcat by doing following (**THIS IS OPTIONAL**):
- Open command line tool (e.g. PowerShell in Windows, remember to run it as administrator)
- Go to your java installation directory and open *"bin"* folder  (in Windows *cd "C:\Program Files\your_java_direcotry\bin"*)
- Run command `keytool.exe -genkey -alias https_key -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore keystore.p12 -validity 3650`
- Enter password and answer to other questions asked
- Copy *"keystore.p12"* file from the directory where you are now to your projects root directory
- Open *"application.properties"* and add the folowing lines:
```java
server.ssl.key-store=keystore.p12
server.ssl.key-store-password=your_keys_password
server.ssl.keyStoreType=PKCS12
server.ssl.keyAlias=https_key
server.port=8443
```
- Restart the application
- Go to URL http://localhost:8443
- You can now see that HTTPS is now enabled
- Your browser is going to complain that the certificate is not trusted etc. because it is now installed in the Trusted Root Certification Authorities store

### Issue 3: A7-Missing Function Level Access Control
**Steps to reproduce:**

1. Go to URL http://localhost:8080
2. Add some text to *"name"* and *"address"* fields and click *"Submit"*
3. Click *"Return to registration page"* 
4. Click *"Login"*
5. Enter *"admin"* as username and correct password what you found in **"Issue 2: A6-Sensitive Data Exposure"** part and click *"Login"*
6. You can now see *"Participants"* link below *"Login"* link
7. Click *"Participants"* link
8. You can now see the participant what you added
9. Restart the application
10. Repeat steps 1-3 and do not login
11. Go to URL http://localhost:8080/participants
12. You can see all the participants and remove them without being logged in as admin

**Solution:**
- Configure security settings to prevent access to http://localhost:8080/participants page without being logged in 
- Open `"SecurityConfiguration.java"` and comment or remove lines: 
```java
   http.authorizeRequests()
       .anyRequest().permitAll();
```
- Uncomment lines:
```java
	http.authorizeRequests()
       .antMatchers("/participants/**").authenticated();
```
- Restart the application
- Go to URL http://localhost:8080/participants
- Now the *"participants"* page requires authentication


### Issue 4: A3-Cross-Site Scripting (XSS)
**Steps to reproduce:**

1. Go to URL http://localhost:8080
2. Add some text to *"name"* field
3. Add text `<script>alert('PWNED');</script>` to *"address"* field
4. Go to URL http://localhost:8080/participants
5. You can now see pop-up window with text 'PWNED'

**Solution:**
- Open *"participants.html"* in a editor of your choice
- Change `"th:utext"` span fields to `"th:text"`
- Restart the application
- Repeat steps 1-4 and verify that you cannot see pop-up window with text 'PWNED' anymore


### Issue 5: A5-Security Misconfiguration
**Steps to reproduce:**

1. Go to URL http://localhost:8080
2. Edit the URL to http://localhost:8080/form/thispagedoesnotexist
3. You can now see custom error page which includes sensitive information what should not be there

**Solution:**
- Open `"error.html"` in a editor of your choice and remove the sensitive information from it
- Restart the application
- Repeat steps 1-2 and verify that you cannot see sensitive information anymore
