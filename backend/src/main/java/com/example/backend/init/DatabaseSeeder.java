package com.example.backend.init;

import com.example.backend.dto.response.location.LocationJsonDto;
import com.example.backend.entity.Locations;
import com.example.backend.repository.location.LocationsRepository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.example.backend.entity.Role;
import com.example.backend.entity.Users;
import com.example.backend.repository.user.RoleRepository;
import com.example.backend.repository.user.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final LocationsRepository locationsRepository;
    private final ObjectMapper objectMapper;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Khởi tạo vai trò và tài khoản Admin mặc định
        seedRolesAndAdmin();

        // Chỉ chạy khi bảng rỗng
        if (locationsRepository.count() == 0) {
            seedData();
        }
    }

    private void seedRolesAndAdmin() {
        Role adminRole = roleRepository.findById("ADMIN").orElseGet(() -> {
            Role role = Role.builder()
                    .id("ADMIN")
                    .name("ADMIN")
                    .description("Hệ thống quản trị tối cao")
                    .build();
            return roleRepository.save(role);
        });

        Role userRole = roleRepository.findById("USER").orElseGet(() -> {
            Role role = Role.builder()
                    .id("USER")
                    .name("USER")
                    .description("Thành viên tiêu chuẩn")
                    .build();
            return roleRepository.save(role);
        });

        if (!userRepository.findByEmail("admin@photoscout.com").isPresent()) {
            Users admin = Users.builder()
                    .username("admin")
                    .email("admin@photoscout.com")
                    .password(passwordEncoder.encode("admin123"))
                    .level(100)
                    .reputationScore(1000)
                    .roles(List.of(adminRole))
                    .unreadNotificationCount(0L)
                    .build();
            userRepository.save(admin);
            System.out.println("✅ Tự động khởi tạo tài khoản quản trị: admin@photoscout.com / admin123");
        }
    }

    private void seedData() throws Exception {
        // 1️⃣ ĐỌC DỮ LIỆU TỪ 2 FILE JSON
        InputStream provIs = new ClassPathResource("province.json").getInputStream();
        Map<String, LocationJsonDto> provinceJsonMap = objectMapper.readValue(provIs, new TypeReference<>() {}); //

        InputStream wardIs = new ClassPathResource("ward.json").getInputStream();
        Map<String, LocationJsonDto> wardJsonMap = objectMapper.readValue(wardIs, new TypeReference<>() {}); //

        // 2️⃣ TẠO VÀ LƯU DANH SÁCH TỈNH (LEVEL 0)
        List<Locations> provinces = new ArrayList<>();
        for (LocationJsonDto dto : provinceJsonMap.values()) {
            Locations province = new Locations();
            province.setName(dto.getName());
            province.setCode(dto.getCode()); //
            province.setType(dto.getType()); //
            province.setSlug(dto.getSlug()); //
            province.setNameWithType(dto.getNameWithType()); //
            province.setLevel(0); // Cấp Tỉnh
            provinces.add(province);
        }

        // Lưu HÀNG LOẠT 63 tỉnh (chỉ tốn 1 câu lệnh SQL)
        List<Locations> savedProvinces = locationsRepository.saveAll(provinces);
        System.out.println("✅ Đã lưu xong dữ liệu Tỉnh/Thành phố!");

        // 3️⃣ TẠO "TỪ ĐIỂN" TRA CỨU TỈNH TRÊN RAM (HashMap)
        // Dùng mã code làm Key để tìm đối tượng Tỉnh cực nhanh
        Map<String, Locations> provinceDictionary = new HashMap<>();
        for (Locations p : savedProvinces) {
            provinceDictionary.put(p.getCode(), p);
        }

        // 4️⃣ TẠO VÀ LƯU DANH SÁCH XÃ (LEVEL 1)
        List<Locations> wards = new ArrayList<>();
        for (LocationJsonDto dto : wardJsonMap.values()) {
            Locations ward = new Locations();
            ward.setName(dto.getName());
            ward.setCode(dto.getCode()); //
            ward.setType(dto.getType()); //
            ward.setSlug(dto.getSlug()); //
            ward.setNameWithType(dto.getNameWithType()); //
            ward.setLevel(1); // Cấp Xã

            // Tra cứu Tỉnh từ "từ điển" trên RAM bằng parent_code
            Locations parentProvince = provinceDictionary.get(dto.getParentCode());
            if (parentProvince != null) {
                ward.setParent(parentProvince);
                wards.add(ward);
            }
        }

        // Lưu HÀNG LOẠT hơn 10.000 xã vào Database
        locationsRepository.saveAll(wards);
        System.out.println("✅ Đã lưu xong toàn bộ dữ liệu Phường/Xã!");
    }
}
