package hello.itemservice.web.item.basic;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;

@Controller
@RequestMapping("/basic/items")
@RequiredArgsConstructor
public class BasicItemController {
    private final ItemRepository itemRepository;
    @GetMapping
    public String items(Model model){
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "basic/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable Long itemId, Model model){
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/item";
    }

    @GetMapping("/add")
    public String addForm(){
        return "basic/addForm";
    }

//    @PostMapping("/add")
//    public String addItemV1(
//            @RequestParam String itemName,
//            @RequestParam int price,
//            @RequestParam Integer quantity,
//            Model model){
//        Item item = new Item();
//        item.setItemName(itemName);
//        item.setPrice(price);
//        item.setQuantity(quantity);
//
//        itemRepository.save(item);
//
//        model.addAttribute("item", item);
//
//        return "basic/item";
//    }

//    @PostMapping("/add")
//    public String addItemV2(@ModelAttribute Item item, Model model){
//        itemRepository.save(item);
//        model.addAttribute("item", item);
//        return "basic/item";
//    }

//    @PostMapping("/add")
//    public String addItemV3(@ModelAttribute Item item){
//        itemRepository.save(item);
//        return "basic/item";
//    }

//    @PostMapping("/add")
//    public String addItemV4(@ModelAttribute("hello") Item item){
//        itemRepository.save(item);
//        return "basic/item";
//    } -> 오류난다. 왜냐하면 basic/item에서 hello라는 데이터를 요구하지 않기 때문이다.

//    @PostMapping("/add")
//    public String addItemV5(@ModelAttribute("item") Item item){
//        itemRepository.save(item);
//        return "basic/item";
//    }
//    @ModelAttribute는 중요한 한가지 기능이 더 있는데, 바로 모델(Model)에 @ModelAttribute로
//    지정한 객체를 자동으로 넣어준다. 지금 코드를 보면 model.addAttribute("item", item)이
//    없는 것을 확인할 수 있는데 없어도 잘 동작하는 것을 확인할 수 있다.
//    모델에 데이터를 담을 때는 이름이 필요하다. 이름은 @ModelAttribute에 지정한 name(value) 속성을 사용한다.
//    만약 다음과 같이 @ModelAttribute의 이름을 다르게 지정하면 다른 이름으로 모델에 포함된다.
//    @ModelAttribute("hello") Item item   ->   이름을 hello로 지정
//    @ModelAttribute("hello", item);      ->   모델에 hello이름으로 저장

//    @PostMapping("/add")
//    public String addItemV6(Item item){
//        itemRepository.save(item);
//        return "basic/item";
//    } // -> @ModelAttribute 전체(자체)를 생략해도 정상적으로 동작한다.

    @PostMapping("/add")
    public String addItemV7(Item item){
        itemRepository.save(item);
        return "redirect:/basic/items/"+item.getId();
//    ->  url은 띄어쓰기나 한글이 들어가면 안되는데
//      항상 인코딩 해서 넘겨야 한다. id, 숫자는
//      상관없어서 이번 상황에서는 괜찮지만 이렇게 item.getId()
//      써주는 것은 위험한 것이다.
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model){
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item){
        itemRepository.update(itemId, item);
        return "redirect:/basic/items/{itemId}";
    }

    @PostConstruct
    public void init(){
        itemRepository.save(new Item("testA", 10000, 10));
        itemRepository.save(new Item("testB", 20000, 10));
    }
}
