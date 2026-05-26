package com.example.data.model

import java.io.Serializable

data class Product(
    val id: String,
    val name: String,
    val category: String,
    val price: Double,
    val weight: String,
    val emoji: String,
    val description: String,
    val calories: Int,
    val proteins: Double,
    val fats: Double,
    val carbs: Double,
    val rating: Double = 4.8
) : Serializable

val PREDEFINED_PRODUCTS = listOf(
    // Vegetables & Fruits (Овощи и Фрукты)
    Product(
        id = "v1",
        name = "Томаты Черри спелые",
        category = "Овощи и Фрукты",
        price = 149.0,
        weight = "250 г",
        emoji = "🍅",
        description = "Сладкие и сочные мини-томаты черри, идеально подходящие для легких салатов или в качестве полезного перекуса.",
        calories = 22,
        proteins = 1.1,
        fats = 0.2,
        carbs = 3.8
    ),
    Product(
        id = "v2",
        name = "Огурцы короткоплодные",
        category = "Овощи и Фрукты",
        price = 119.0,
        weight = "450 г",
        emoji = "🥒",
        description = "Хрустящие, ароматные грунтовые огурцы с тонкой кожицей. Отличный выбор для свежих летних салатов.",
        calories = 15,
        proteins = 0.8,
        fats = 0.1,
        carbs = 2.8,
        rating = 4.9
    ),
    Product(
        id = "v3",
        name = "Бананы Эквадор",
        category = "Овощи и Фрукты",
        price = 159.0,
        weight = "1 кг",
        emoji = "🍌",
        description = "Спелые, сладкие бананы премиум качества, богатые калием. Быстрый и удобный источник энергии.",
        calories = 95,
        proteins = 1.5,
        fats = 0.2,
        carbs = 21.8
    ),
    Product(
        id = "v4",
        name = "Яблоки Ред Делишес",
        category = "Овощи и Фрукты",
        price = 189.0,
        weight = "1 кг",
        emoji = "🍎",
        description = "Хрустящие сладкие яблоки насыщенно-красного цвета. Сочные, с плотной мякотью и прекрасным ароматом.",
        calories = 47,
        proteins = 0.4,
        fats = 0.4,
        carbs = 11.2,
        rating = 4.7
    ),
    Product(
        id = "v5",
        name = "Авокадо Хасс спелое",
        category = "Овощи и Фрукты",
        price = 199.0,
        weight = "2 шт",
        emoji = "🥑",
        description = "Спелое авокадо сорта Хасс с нежным маслянистым вкусом. Содержит полезные жиры и витамины группы B.",
        calories = 160,
        proteins = 2.0,
        fats = 14.7,
        carbs = 8.5,
        rating = 4.9
    ),

    // Dairy & Eggs (Молоко и Яйца)
    Product(
        id = "d1",
        name = "Молоко пастеризованное 3.2%",
        category = "Молоко и Яйца",
        price = 89.0,
        weight = "930 мл",
        emoji = "🥛",
        description = "Натуральное питьевое коровье молоко пастеризованное. Содержит все полезные витамины и микроэлементы.",
        calories = 60,
        proteins = 3.0,
        fats = 3.2,
        carbs = 4.7
    ),
    Product(
        id = "d2",
        name = "Яйца куриные С0, 10 шт",
        category = "Молоко и Яйца",
        price = 139.0,
        weight = "10 шт",
        emoji = "🥚",
        description = "Крупные отборные куриные яйца С0 птицефабрики высшего качества. Богатство фермерского белка.",
        calories = 157,
        proteins = 12.7,
        fats = 11.5,
        carbs = 0.7,
        rating = 4.9
    ),
    Product(
        id = "d3",
        name = "Творог рассыпчатый 9%",
        category = "Молоко и Яйца",
        price = 165.0,
        weight = "300 г",
        emoji = "🍚",
        description = "Нежный рассыпчатый творог девятипроцентной жирности из натурального молока без добавления растительных жиров.",
        calories = 157,
        proteins = 16.0,
        fats = 9.0,
        carbs = 3.0
    ),
    Product(
        id = "d4",
        name = "Сливочное масло 82.5%",
        category = "Молоко и Яйца",
        price = 219.0,
        weight = "180 г",
        emoji = "🧈",
        description = "Качественное традиционное сливочное масло, изготовленное исключительно из свежих сливок.",
        calories = 748,
        proteins = 0.6,
        fats = 82.5,
        carbs = 0.8
    ),

    // Bread & Bakery (Выпечка и Хлеб)
    Product(
        id = "b1",
        name = "Багет французский хрустящий",
        category = "Хлеб и Выпечка",
        price = 59.0,
        weight = "250 г",
        emoji = "🥖",
        description = "Свежевыпеченный классический багет с хрустящей корочкой и воздушным пористым мякишем внутри.",
        calories = 262,
        proteins = 7.5,
        fats = 2.9,
        carbs = 51.4
    ),
    Product(
        id = "b2",
        name = "Круассан классический",
        category = "Хлеб и Выпечка",
        price = 79.0,
        weight = "70 г",
        emoji = "🥐",
        description = "Настоящий французский круассан на сливочном масле. Слоеный, воздушный и невероятно нежный.",
        calories = 406,
        proteins = 8.2,
        fats = 21.0,
        carbs = 45.8,
        rating = 4.9
    ),
    Product(
        id = "b3",
        name = "Хлеб Бородинский",
        category = "Хлеб и Выпечка",
        price = 45.0,
        weight = "350 г",
        emoji = "🍞",
        description = "Традиционный ржаной заварной хлеб со специями (тмином и кориандром), обладающий характерным пикантным вкусом.",
        calories = 208,
        proteins = 6.8,
        fats = 1.3,
        carbs = 40.0
    ),

    // Meat & Poultry (Мясо и Птица)
    Product(
        id = "m1",
        name = "Филе куриной грудки охлажденное",
        category = "Мясо и Птица",
        price = 379.0,
        weight = "800 г",
        emoji = "🍗",
        description = "Диетическое охлажденное филе куриного белого мяса. Отличная основа для запекания, варки или жарки.",
        calories = 113,
        proteins = 23.6,
        fats = 1.9,
        carbs = 0.4
    ),
    Product(
        id = "m2",
        name = "Стейк Рибай мраморная говядина",
        category = "Мясо и Птица",
        price = 699.0,
        weight = "350 г",
        emoji = "🥩",
        description = "Премиальный стейк Рибай из высококачественной мраморной говядины зернового откорма. Сочный и мягкий.",
        calories = 260,
        proteins = 18.0,
        fats = 21.0,
        carbs = 0.0,
        rating = 5.0
    ),
    Product(
        id = "m3",
        name = "Фарш из индейки легкий",
        category = "Мясо и Птица",
        price = 249.0,
        weight = "450 г",
        emoji = "🍖",
        description = "Охлажденный диетический мясной фарш из филе грудки и бедра индейки. Нежный, сочный и низкокалорийный.",
        calories = 160,
        proteins = 19.0,
        fats = 9.0,
        carbs = 0.0
    ),

    // Greenery (Зелень)
    Product(
        id = "g1",
        name = "Микс салатов Трио",
        category = "Зелень",
        price = 129.0,
        weight = "150 г",
        emoji = "🥗",
        description = "Сочная хрустящая смесь свежих салатных листьев (Романо, Айсберг, Радиччио). Полностью готовы к употреблению.",
        calories = 17,
        proteins = 1.5,
        fats = 0.2,
        carbs = 2.3
    ),
    Product(
        id = "g2",
        name = "Укроп и петрушка свежие",
        category = "Зелень",
        price = 69.0,
        weight = "100 г",
        emoji = "🌿",
        description = "Ароматный букет свежей зелени укропа и петрушки, собранный прямо с грядок для ваших любимых блюд.",
        calories = 38,
        proteins = 2.5,
        fats = 0.4,
        carbs = 6.0
    ),

    // Drinks (Напитки)
    Product(
        id = "dr1",
        name = "Сок яблочный прямого отжима",
        category = "Напитки",
        price = 159.0,
        weight = "1 л",
        emoji = "🍹",
        description = "Натуральный яблочный сок прямого отжима без добавления сахара, красителей и консервантов. Сладкий от природы.",
        calories = 46,
        proteins = 0.0,
        fats = 0.0,
        carbs = 11.5
    ),
    Product(
        id = "dr2",
        name = "Минеральная вода Borjomi",
        category = "Напитки",
        price = 99.0,
        weight = "750 мл",
        emoji = "💧",
        description = "Легендарная лечебно-столовая минеральная вода вулканического происхождения, насыщенная микроэлементами.",
        calories = 0,
        proteins = 0.0,
        fats = 0.0,
        carbs = 0.0,
        rating = 4.9
    )
)
