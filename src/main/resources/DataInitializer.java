package com.example.demo.config;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;

@Component
@Profile("dev")  // Только для dev профиля
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final StopRepository stopRepository;
    private final RouteRepository routeRepository;
    private final BusRepository busRepository;
    private final PassengerCountRepository passengerCountRepository;
    
    public DataInitializer(UserRepository userRepository, RoleRepository roleRepository,
                          PermissionRepository permissionRepository, PasswordEncoder passwordEncoder,
                          StopRepository stopRepository, RouteRepository routeRepository,
                          BusRepository busRepository, PassengerCountRepository passengerCountRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.passwordEncoder = passwordEncoder;
        this.stopRepository = stopRepository;
        this.routeRepository = routeRepository;
        this.busRepository = busRepository;
        this.passengerCountRepository = passengerCountRepository;
    }
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("🚀 Инициализация тестовых данных...");
        
        // Проверяем, есть ли уже пользователи
        if (userRepository.count() > 0) {
            System.out.println("✅ Данные уже инициализированы");
            return;
        }
        
        // Создаем permissions
        System.out.println("📋 Создание permissions...");
        Permission busRead = createPermission("BUS", "READ");
        Permission busCreate = createPermission("BUS", "CREATE");
        Permission busUpdate = createPermission("BUS", "UPDATE");
        Permission busDelete = createPermission("BUS", "DELETE");
        
        Permission stopRead = createPermission("STOP", "READ");
        Permission stopCreate = createPermission("STOP", "CREATE");
        Permission stopUpdate = createPermission("STOP", "UPDATE");
        Permission stopDelete = createPermission("STOP", "DELETE");
        
        Permission passengerRead = createPermission("PASSENGER", "READ");
        Permission passengerCreate = createPermission("PASSENGER", "CREATE");
        Permission passengerUpdate = createPermission("PASSENGER", "UPDATE");
        Permission passengerDelete = createPermission("PASSENGER", "DELETE");
        
        Permission reportRead = createPermission("REPORT", "READ");
        Permission reportCreate = createPermission("REPORT", "CREATE");
        
        // Создаем роли
        System.out.println("👑 Создание ролей...");
        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        adminRole.setPermissions(new HashSet<>(Arrays.asList(
            busRead, busCreate, busUpdate, busDelete,
            stopRead, stopCreate, stopUpdate, stopDelete,
            passengerRead, passengerCreate, passengerUpdate, passengerDelete,
            reportRead, reportCreate
        )));
        adminRole = roleRepository.save(adminRole);
        
        Role managerRole = new Role();
        managerRole.setName("MANAGER");
        managerRole.setPermissions(new HashSet<>(Arrays.asList(
            busRead, busCreate, busUpdate,
            stopRead, stopCreate, stopUpdate,
            passengerRead, passengerCreate, passengerUpdate,
            reportRead
        )));
        roleRepository.save(managerRole);
        
        // Создаем пользователей
        System.out.println("👤 Создание пользователей...");
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));  // Важно: шифруем пароль
        admin.setEmail("admin@system.com");
        admin.setRole(adminRole);
        userRepository.save(admin);
        
        User manager = new User();
        manager.setUsername("manager");
        manager.setPassword(passwordEncoder.encode("manager123"));
        manager.setEmail("manager@system.com");
        manager.setRole(managerRole);
        userRepository.save(manager);
        
        // Тестовые данные для системы
        System.out.println("🚌 Создание тестовых данных...");
        Stop stop1 = new Stop();
        stop1.setName("Центральная");
        stop1.setLat(55.7558);
        stop1.setLon(37.6176);
        stopRepository.save(stop1);
        
        Stop stop2 = new Stop();
        stop2.setName("Северная");
        stop2.setLat(55.7658);
        stop2.setLon(37.6276);
        stopRepository.save(stop2);
        
        Route route = new Route();
        route.setStops(Arrays.asList(stop1, stop2));
        routeRepository.save(route);
        
        Bus bus = new Bus();
        bus.setModel("Mercedes Sprinter");
        bus.setRoute(route);
        busRepository.save(bus);
        
        PassengerCount pc = new PassengerCount();
        pc.setBus(bus);
        pc.setStop(stop1);
        pc.setEntered(15);
        pc.setExited(8);
        pc.setTimestamp(LocalDateTime.now().minusHours(2));
        passengerCountRepository.save(pc);
        
        System.out.println("=".repeat(50));
        System.out.println("✅ Данные успешно инициализированы!");
        System.out.println("👤 Администратор: admin / admin123");
        System.out.println("👤 Менеджер: manager / manager123");
        System.out.println("=".repeat(50));
    }
    
    private Permission createPermission(String resource, String operation) {
        Permission permission = new Permission();
        permission.setResource(resource);
        permission.setOperation(operation);
        return permissionRepository.save(permission);
    }
}