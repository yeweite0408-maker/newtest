package com.mall.config;

import com.mall.entity.Product;
import com.mall.entity.User;
import com.mall.mapper.ProductMapper;
import com.mall.mapper.UserMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final ProductMapper productMapper;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public DataInitializer(UserMapper userMapper, ProductMapper productMapper,
                           org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.productMapper = productMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Create admin if not exists
        if (userMapper.findByUsername("admin") == null) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setEmail("admin@game-mall.com");
            admin.setRole("admin");
            userMapper.insert(admin);
            System.out.println("Admin account created: admin / admin");
        }

        // Create test user if not exists
        if (userMapper.findByUsername("test") == null) {
            User test = new User();
            test.setUsername("test");
            test.setPassword(passwordEncoder.encode("123456"));
            test.setEmail("test@game-mall.com");
            test.setRole("user");
            userMapper.insert(test);
            System.out.println("Test user created: test / 123456");
        }

        // Seed 20 Steam games if table is empty
        if (productMapper.findAll().isEmpty()) {
            seedGames();
            System.out.println("20 Steam games seeded.");
        }
    }

    private void seedGames() {
        insertGame("Dota 2", 0, "MOBA", "Valve",
                "一款免费的多人在线战斗竞技场游戏，两支队伍各5名英雄对战，摧毁对方远古遗迹。",
                "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/570/header.jpg");
        insertGame("Counter-Strike 2", 0, "FPS", "Valve",
                "革命性的第一人称射击游戏，使用起源2引擎打造，带来真实的射击体验和竞技对抗。",
                "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/730/header.jpg");
        insertGame("PUBG: BATTLEGROUNDS", 98, "大逃杀", "KRAFTON",
                "大逃杀类游戏的开拓者，100名玩家空降到孤岛上进行生存竞技。",
                "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/578080/header.jpg");
        insertGame("Apex Legends", 0, "大逃杀", "Respawn Entertainment",
                "免费的大逃杀射击游戏，拥有独特的英雄技能系统。",
                "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/1172470/header.jpg");
        insertGame("Rust", 116, "生存", "Facepunch Studios",
                "一款多人生存游戏，在严酷的环境中收集资源、建造基地、抵御威胁。",
                "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/252490/header.jpg");
        insertGame("Grand Theft Auto V", 118, "开放世界", "Rockstar North",
                "备受赞誉的开放世界动作冒险游戏，在洛圣都体验三位主角交织的犯罪故事。",
                "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/271590/header.jpg");
        insertGame("ELDEN RING", 298, "动作RPG", "FromSoftware",
                "宫崎英高与乔治·R·R·马丁联手打造的黑暗奇幻动作RPG。",
                "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/1245620/header.jpg");
        insertGame("Cyberpunk 2077", 299, "开放世界RPG", "CD PROJEKT RED",
                "在夜之城的黑暗未来中，成为义体改造的雇佣兵。",
                "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/1091500/header.jpg");
        insertGame("Red Dead Redemption 2", 249, "开放世界", "Rockstar Studios",
                "史诗般的西部冒险游戏，讲述亚瑟·摩根的亡命之旅。",
                "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/1174180/header.jpg");
        insertGame("Baldur's Gate 3", 298, "CRPG", "Larian Studios",
                "基于龙与地下城规则的回合制CRPG巨作。",
                "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/1086940/header.jpg");
        insertGame("Hogwarts Legacy", 298, "开放世界RPG", "Avalanche Software",
                "在19世纪的霍格沃茨中学习魔法、探索奥秘。",
                "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/990080/header.jpg");
        insertGame("Call of Duty: Modern Warfare III", 469, "FPS", "Sledgehammer Games",
                "使命召唤系列最新作，延续现代战争系列的紧张战役。",
                "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/2519060/header.jpg");
        insertGame("EA SPORTS FC 24", 249, "体育", "EA Canada",
                "全球最受欢迎的足球游戏系列，使用HyperMotionV技术。",
                "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/2195250/header.jpg");
        insertGame("Rainbow Six Siege", 88, "战术FPS", "Ubisoft Montreal",
                "战术竞技射击游戏，主打近距离团队合作和破坏机制。",
                "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/359550/header.jpg");
        insertGame("The Witcher 3: Wild Hunt", 127, "动作RPG", "CD PROJEKT RED",
                "扮演猎魔人杰洛特，在黑暗奇幻世界中寻找命运。",
                "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/292030/header.jpg");
        insertGame("Sid Meier's Civilization VI", 199, "策略", "Firaxis Games",
                "带领你的文明从石器时代走向信息时代。",
                "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/289070/header.jpg");
        insertGame("Stardew Valley", 48, "模拟经营", "ConcernedApe",
                "温馨的像素风农场经营模拟游戏。",
                "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/413150/header.jpg");
        insertGame("Left 4 Dead 2", 42, "FPS", "Valve",
                "经典丧尸题材第一人称射击游戏。",
                "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/550/header.jpg");
        insertGame("Team Fortress 2", 0, "FPS", "Valve",
                "免费的多人在线团队射击游戏，九种独特职业。",
                "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/440/header.jpg");
        insertGame("Palworld", 108, "开放世界生存", "Pocketpair",
                "与名为帕鲁的神秘生物一起战斗、建造、探索。",
                "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/1623730/header.jpg");
    }

    private void insertGame(String name, double price, String category, String developer, String description, String image) {
        Product p = new Product();
        p.setName(name);
        p.setPrice(BigDecimal.valueOf(price));
        p.setCategory(category);
        p.setDeveloper(developer);
        p.setDescription(description);
        p.setImage(image);
        p.setStock(price == 0 ? 99999 : 999);
        productMapper.insert(p);
    }
}
