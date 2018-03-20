package com.capstone.controllers;

import com.capstone.entities.CredentialsEntity;
import com.capstone.entities.CredentialsRolesEntity;
import com.capstone.entities.DynamicMenuEntity;
import com.capstone.models.CustomUser;
import com.capstone.models.GoogleProfile;
import com.capstone.models.Logger;
import com.capstone.models.Ultilities;
import com.capstone.services.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class LoginController implements ServletContextAware {

    private ServletContext context;

    @Autowired
    @Qualifier("rememberMeAuthenticationProvider")
    private PersistentTokenBasedRememberMeServices rememberMeServices;

    @RequestMapping("/favicon.ico")
    public String Redirect() {
        return "redirect:/";
    }

    @RequestMapping("/register")
    public String Register() {
        return "Register";
    }

    @RequestMapping("/googleSignIn")
    public RedirectView methodSignIn(HttpServletResponse httpServletResponse, HttpServletRequest request) {
        String hostname = request.getServerName();
        int port = request.getServerPort();

        //local ip của mạng trường k truy cập được ra bên ngoài
        if (hostname.indexOf("localhost") == -1 && hostname.indexOf("xip.io") == -1) {
            hostname += "xip.io";
        }
        String redirectUri = hostname + ":" + port;
        String url = "https://accounts.google.com/o/oauth2/auth?" +
                "client_id=633838326707-anulcphc8kqt0k2hib34r42or6ikgcv8.apps.googleusercontent.com" +
                "&redirect_uri=http://" + redirectUri + "/auth/google" +
                "&scope=openid%20email%20profile&&response_type=code&approval_prompt=auto";

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(url);
        return redirectView;
    }

    // Sign up
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject Register(@RequestBody CredentialsEntity user) {
        ICredentialsService service = new CredentialsServiceImpl();
        JsonObject obj = new JsonObject();
        try {
            CredentialsEntity entity = service.findCredential(user.getUsername());
            if (entity != null) {
                obj.addProperty("success", false);
                obj.addProperty("msg", "User exist!");
                return obj;
            }

            CredentialsEntity entity2 = service.findCredentialByEmail(user.getEmail());
            if (entity2 != null) {
                obj.addProperty("success", false);
                obj.addProperty("msg", "Email đã tồn tại");
                return obj;
            }

            // list of acceptable domains
            String[] domains = {"fpt.edu.vn"};

            String domain = user.getEmail().substring(user.getEmail().indexOf('@') + 1).toLowerCase();
            if (Arrays.asList(domains).contains(domain)) {

                // encoding password with BCrypt which is defined in security.xml
                PasswordEncoder encoder = new BCryptPasswordEncoder();
                String encodedPass = encoder.encode(user.getPassword());
                System.out.println("New password: " + encodedPass);
                user.setPassword(encodedPass);
                service.CreateCredentiall(user);

                obj.addProperty("success", true);
            } else {
                obj.addProperty("success", false);
                obj.addProperty("msg", "Email này không phải của nhà trường!");
            }
        } catch (Exception e) {
            Logger.writeLog(e);

            obj.addProperty("success", false);
            obj.addProperty("msg", e.getMessage());
        }

        return obj;
    }

    @RequestMapping("/deny")
    public String Deny() {
        return "Deny";
    }

    @RequestMapping(value = "/login")
    public String Login() {
        return "Login";
    }

    @RequestMapping(value = "/logout")
    public String Logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login?logout";
    }

    // sign in with google API
    @RequestMapping(value = "/auth/google")
    public String Google(@RequestParam Map<String, String> params, HttpServletRequest request, HttpServletResponse response) {
        try {
            URL obj = new URL("https://www.googleapis.com/oauth2/v4/token");
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // because google does not accept IP address as url, we need to use xip.io as proxy to bypass google until we have a proper address
            String url = request.getHeader("host");
            if (!url.contains("localhost") && !url.contains("xip.io")) {
                url += ".xip.io";
            }
            if (url.split(":").length < 2) {
                url += ":" + request.getServerPort();
            }
            System.out.println(url);

            // google required parameters (see document for more info)
            String POST_PARAMS = "code=" + params.get("code") +
                    "&client_id=633838326707-anulcphc8kqt0k2hib34r42or6ikgcv8.apps.googleusercontent.com" +
                    "&client_secret=_cvE4Tq5ljKZYj5g7LYtOpgJ" +
                    "&redirect_uri=http://" + url + "/auth/google" +
                    "&grant_type=authorization_code";

            con.setDoOutput(true);
            OutputStream os = con.getOutputStream();
            os.write(POST_PARAMS.getBytes());
            os.flush();
            os.close();

            int responseCode = con.getResponseCode();
            System.out.println("POST Response Code : " + responseCode + ", msg: " + con.getResponseMessage());
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer data = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    data.append(inputLine);
                }
                in.close();

                // store result into class
                // because google return base64 data, we need to decode them
                GoogleProfile profile = new Gson().fromJson(data.toString(), GoogleProfile.class);
                String[] split_string = profile.getId_token().split("\\.");
                byte[] valueDecoded = Base64.decodeBase64(split_string[1].getBytes());
                profile = new Gson().fromJson(new String(valueDecoded, "UTF-8"), GoogleProfile.class);

                // check fpt email domain
                String[] domains = {"fpt.edu.vn"};

                String domain = profile.getEmail().substring(profile.getEmail().indexOf('@') + 1).toLowerCase();
                if (Arrays.asList(domains).contains(domain)) {
                    ICredentialsService service = new CredentialsServiceImpl();
                    CredentialsEntity user = service.findCredentialByEmail(profile.getEmail());
                    if (user != null) {
                        boolean edited = false;
                        if (user.getFullname() == null) {
                            user.setFullname(profile.getName());
                            edited = true;
                        }
                        if (user.getPicture() == null || !user.getPicture().equals(profile.getPicture())) {
                            user.setPicture(profile.getPicture());
                            edited = true;
                        }

                        if (edited) service.SaveCredential(user, false);

                        Authentication auth = new UsernamePasswordAuthenticationToken(new CustomUser(user.getUsername(), user.getPassword(), getGrantedAuthorities(user), user),
                                user.getPassword(),
                                getGrantedAuthorities2(user));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request) {
                            @Override
                            public String getParameter(String name) {
                                return "true";
                            }
                        };
                        rememberMeServices.loginSuccess(wrapper, response, auth);

                        Ultilities.GetMenu2(context, user);

                        return "redirect:/";
                    } else {
                        return "redirect:/register?email=" + profile.getEmail() + "&disable=true";
                    }
                } else {
                    String msg = "Email này không phải của nhà trường!";
                    return "redirect:/login?error=" + URLEncoder.encode(msg, "utf-8");
                }
            }
        } catch (Exception e) {
            System.out.println("Error occured: " + e.getMessage() + " (more details at logs)");
            Logger.writeLog(e);
        }

        return "redirect:/";
    }

    private List<GrantedAuthority> getGrantedAuthorities(CredentialsEntity user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        String[] roles = user.getRole().split(",");
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.trim()));
        }
        return authorities;
    }
    private List<GrantedAuthority> getGrantedAuthorities2(CredentialsEntity user){
        List<GrantedAuthority> authorities = new ArrayList<>();
        CredentialsRolesServiceImpl credentialsRolesService = new CredentialsRolesServiceImpl();
        List<CredentialsRolesEntity> roles = credentialsRolesService.getCredentialsRolesByCredentialsId(user.getId());

        for (CredentialsRolesEntity role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getRolesId().getName()));
        }
        return authorities;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        context = servletContext;
    }
}
