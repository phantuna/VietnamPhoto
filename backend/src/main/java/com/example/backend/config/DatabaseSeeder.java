package com.example.backend.config;

import com.example.backend.dto.response.location.LocationJsonDto;
import com.example.backend.entity.Locations;
import com.example.backend.repository.location.LocationsRepository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

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

    @Override
    public void run(String... args) throws Exception {
        // Chỉ chạy khi bảng rỗng
        if (locationsRepository.count() == 0) {
            seedData();
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