package sec.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sec.project.domain.Signup;
import sec.project.repository.SignupRepository;

@Service
public class SignupService {
    
    @Autowired
    SignupRepository signupRepository;
    
    public void submitForm(String name, String address) {
        Signup signup = new Signup();
        signup.setName(name);
        signup.setAddress(address);
        
        
    }
    
}
