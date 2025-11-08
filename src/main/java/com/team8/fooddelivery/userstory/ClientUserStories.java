package com.team8.fooddelivery.userstory;

import com.team8.fooddelivery.model.Shop;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientUserStories {

    public static final List<Shop> SHOPS = new ArrayList<>();

    public static void main(String[] args) {
        checkUserStory1();
        //  checkUserStory2();
    }

      public static void checkUserStory1() {
        log.info(CLIENTS);

         UserService userService =new UserService();
        userService.register(....);

        log.info(CLIENTS);
      }
          
}
//ShopInfoServiceImpl