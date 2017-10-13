package com.capstone.registry;

import com.capstone.entities.CredentialsEntity;
import com.capstone.services.CredentialsServiceImpl;
import com.capstone.services.ICredentialsService;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfile;

/**
 * If no local user associated with the given connection then
 * connection signup will create a new local user from the given connection.
 *
 * @author <a href="mailto:sunil.pulugula@wavemaker.com">Sunil Kumar</a>
 * @since 27/3/16
 */
public class AppConnectionSignUp implements ConnectionSignUp {

    @Override
    public String execute(final Connection<?> connection) {
        ICredentialsService service = new CredentialsServiceImpl();
        UserProfile profile = connection.fetchUserProfile();
        CredentialsEntity entity = new CredentialsEntity();
        entity.setUsername(profile.getUsername());
        entity.setRole("ROLE_USER");
        service.CreateCredentiall(entity);
        return entity.getUsername();
    }
}
