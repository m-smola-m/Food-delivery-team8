package com.team8.fooddelivery;

@Sl4j
public class ClientUserStories {

    public static final List<Client> CLIENTS = new ArrayList<>();

    public static void main(String[] args) {
        checkUserStory1();
        //  checkUserStory2();
    }

      public static void checkUserStory1() {
        log.info(CLIENTS);
        
        userService.register(....);

        log.info(CLIENTS);
      }
          
}
