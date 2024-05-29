package pro.gravit.simplecabinet.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.gravit.simplecabinet.web.dto.user.UserDto;
import pro.gravit.simplecabinet.web.service.DtoService;
import pro.gravit.simplecabinet.web.service.user.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/banmanager")
public class BanManagerController {
    @Autowired
    private UserService service;
    @Autowired
    private DtoService dtoService;

    @GetMapping("/name/{name}")
    public UserDto.UserUUID getByUsername(@PathVariable String name) {
        var  findUuid = service.findByUsername(name);
        return dtoService.toUsernameUuid(findUuid.get());
    }

    @GetMapping("/uuid/{uuidString}")
    public UserDto.UserUUID getByUUID(@PathVariable String  uuidString) {
         var  findUuid = service.findByUUID(stringToUUID(uuidString));
        return dtoService.toUsernameUuid(findUuid.get());
    }

    private UUID stringToUUID (String uuid){
        return UUID.fromString(uuid.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
    }
}
