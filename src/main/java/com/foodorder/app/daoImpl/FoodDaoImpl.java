package com.foodorder.app.daoImpl;

import com.foodorder.app.dao.FoodDao;
import com.foodorder.app.entities.FoodItem;
import com.foodorder.app.enums.FoodCategory;

import java.util.*;

public class FoodDaoImpl implements FoodDao {
    private final List<FoodItem> foodItems = Collections.synchronizedList (new ArrayList<>());
    private static final FoodDaoImpl foodDao = new FoodDaoImpl();
    private static int idGenerator = 1;

    private void initFoodItems() {
        List<FoodItem> foodItemList = new ArrayList<>();
        foodItemList.add(FoodItem.builder().name("PaneerTikka").price(150.00).category(FoodCategory.VEG).restaurantId(1001).build());
        foodItemList.add(FoodItem.builder().name("ChickenCurry").price(130.00).category(FoodCategory.NONVEG).restaurantId(1001).build());
        foodItemList.add(FoodItem.builder().name("DumBiryani").price(200.00).category(FoodCategory.NONVEG).restaurantId(1001).build());
        foodItemList.add(FoodItem.builder().name("Lassi").price(40.00).category(FoodCategory.BEVERAGES).restaurantId(1001).build());
        foodItemList.add(FoodItem.builder().name("Pasta").price(60.00).category(FoodCategory.VEG).restaurantId(1001).build());
        foodItemList.add(FoodItem.builder().name("ButterMilk").price(40.00).category(FoodCategory.BEVERAGES).restaurantId(1001).build());
        foodItems.addAll(foodItemList);
    }

    private FoodDaoImpl() {
        initFoodItems();
    }

    public static FoodDaoImpl getFoodDaoImpl() {
        return foodDao;
    }

    @Override
    public boolean saveFood(FoodItem foodItem) {
        foodItem.setId(getIdGenerator());
        return foodItems.add(foodItem);
    }

    @Override
    public Optional<FoodItem> getFoodById(int id) {
        return foodItems.stream()
                .filter(f -> f.getId() == id)
                .findFirst();
    }

    @Override
    public List<FoodItem> getAllFood() {
        if (foodItems.isEmpty()) {
            return Collections.emptyList();
        }
        return List.copyOf(foodItems.stream().sorted(Comparator.comparing(FoodItem::getCategory)).toList());
    }

    @Override
    public List<FoodItem> getFoodByCategory(FoodCategory category) {
        if (foodItems.isEmpty()) {
            return Collections.emptyList();
        }
        return foodItems.stream()
                .filter(f -> f.getCategory() == category)
                .toList();
    }

    @Override
    public boolean updateFood(FoodItem foodItem) {
        Optional<FoodItem> getOptionalFood = foodItems.stream()
                .filter(f -> Objects.equals(f.getId(), foodItem.getId()))
                .findFirst();

        if (getOptionalFood.isPresent()) {
            FoodItem f = getOptionalFood.get();
            f.setName(foodItem.getName());
            f.setPrice(foodItem.getPrice());
            f.setCategory(foodItem.getCategory());
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteFood(FoodItem foodItem) {
        return foodItems.removeIf(f->f.getName().equalsIgnoreCase(foodItem.getName()));
    }
    private static int getIdGenerator() {
        return idGenerator++;
    }
}