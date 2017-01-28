package sec.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import sec.project.repository.SignupRepository;

@Controller
public class ParticipantController {
    
    @Autowired
    SignupRepository signupRepository;
    
    @RequestMapping(value = "/participants", method = RequestMethod.GET)
    public String getParticipants(Model model) {
        model.addAttribute("participants", signupRepository.findAll());
        return "participants";
    }
    
    @RequestMapping(value = "/participants/delete/{id}", method = RequestMethod.DELETE)
    public String deleteParticipant(@PathVariable Long id) {
        signupRepository.delete(id);
        return "redirect:/participants";
    }
    
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "login";
    }
}
