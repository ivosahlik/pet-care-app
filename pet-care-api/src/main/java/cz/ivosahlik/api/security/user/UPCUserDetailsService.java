package cz.ivosahlik.api.security.user;

import cz.ivosahlik.api.model.User;
import cz.ivosahlik.api.repository.UserRepository;
import cz.ivosahlik.api.utils.FeedBackMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UPCUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(FeedBackMessage.RESOURCE_NOT_FOUND));
        return UPCUserDetails.buildUserDetails(user);
    }
}


