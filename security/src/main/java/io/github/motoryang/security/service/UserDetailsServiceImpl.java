package io.github.motoryang.security.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 用户详情服务实现
 */
@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    /**
     * 加载用户详情
     * 这个方法在各业务服务中需要被重写，从数据库加载用户信息
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.warn("UserDetailsServiceImpl.loadUserByUsername 需要在业务服务中被重写");
        throw new UsernameNotFoundException("用户不存在");
    }
}
