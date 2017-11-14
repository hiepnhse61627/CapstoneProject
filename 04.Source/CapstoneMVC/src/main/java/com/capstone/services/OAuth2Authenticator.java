package com.capstone.services;

import com.sun.mail.imap.IMAPSSLStore;
import com.sun.mail.imap.IMAPStore;
import com.sun.mail.smtp.SMTPTransport;

import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import java.security.Provider;
import java.security.Security;
import java.util.Properties;

public class OAuth2Authenticator  {

    public static final class OAuth2Provider extends Provider {
        private static final long serialVersionUID = 1L;

        public OAuth2Provider() {
            super("Google OAuth2 Provider", 1.0,
                    "Provides the XOAUTH2 SASL Mechanism");
            put("SaslClientFactory.XOAUTH2",
                    "com.capstone.services.OAuth2SaslClientFactory");
        }
    }

    /**
     * Installs the OAuth2 SASL provider. This must be called exactly once before
     * calling other methods on this class.
     */
    public static void initialize() {
        Security.addProvider(new OAuth2Provider());
    }

    /**
     * Connects and authenticates to an IMAP server with OAuth2. You must have
     * called {@code initialize}.
     *
     * @param host Hostname of the imap server, for example {@code
     *     imap.googlemail.com}.
     * @param port Port of the imap server, for example 993.
     * @param userEmail Email address of the user to authenticate, for example
     *     {@code oauth@gmail.com}.
     * @param oauthToken The user's OAuth token.
     * @param debug Whether to enable debug logging on the IMAP connection.
     *
     * @return An authenticated IMAPStore that can be used for IMAP operations.
     */
    public static Store connectToImap(String host,
                                          int port,
                                          String userEmail,
                                          String oauthToken,
                                          boolean debug) throws Exception {
        Properties props = new Properties();
        props.put("mail.imaps.sasl.enable", "true");
        props.put("mail.imaps.sasl.mechanisms", "XOAUTH2");
        props.put(OAuth2SaslClientFactory.OAUTH_TOKEN_PROP, oauthToken);
        Session session = Session.getInstance(props);
        session.setDebug(debug);

        final URLName unusedUrlName = null;
        Store store = new IMAPSSLStore(session, unusedUrlName);
        final String emptyPassword = "";
        store.connect(host, port, userEmail, emptyPassword);

        return store;
    }

    /**
     * Connects and authenticates to an SMTP server with OAuth2. You must have
     * called {@code initialize}.
     *
     * @param host Hostname of the smtp server, for example {@code
     *     smtp.googlemail.com}.
     * @param port Port of the smtp server, for example 587.
     * @param userEmail Email address of the user to authenticate, for example
     *     {@code oauth@gmail.com}.
     * @param oauthToken The user's OAuth token.
     * @param debug Whether to enable debug logging on the connection.
     *
     * @return An authenticated SMTPTransport that can be used for SMTP
     *     operations.
     */
    private static Session session;

    public static Session getSession() {
        return session;
    }

    public static SMTPTransport connectToSmtp(String host,
                                              int port,
                                              String userEmail,
                                              String oauthToken,
                                              boolean debug) throws Exception {
        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.sasl.enable", "true");
        props.put("mail.smtp.sasl.mechanisms", "XOAUTH2");
        props.put(OAuth2SaslClientFactory.OAUTH_TOKEN_PROP, oauthToken);
        session = Session.getInstance(props);
        session.setDebug(debug);

        final URLName unusedUrlName = null;
        SMTPTransport transport = new SMTPTransport(session, unusedUrlName);
        final String emptyPassword = "";
        transport.connect(host, port, userEmail, emptyPassword);

        return transport;
    }
}