package jenakxm.myblog.service;

import jenakxm.myblog.domain.User;
import jenakxm.myblog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
// 스프링 시큐리티에서 사용자 정보를 가져옴
public class UserDetailService implements UserDetailsService {      // 로그인용 서비스
    private final UserRepository userRepository;

    // 사용자 이메일로 사용자의 정보를 가져옴
    @Override
    public User loadUserByUsername(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(email));
    }
}
