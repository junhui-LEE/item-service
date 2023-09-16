package hello.itemservice.web.item.basic;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

//    @PostMapping("/add")
//    public String addItemV7(Item item){
//        itemRepository.save(item);
//        return "redirect:/basic/items/"+item.getId();
////    ->  url은 띄어쓰기나 한글이 들어가면 안되는데
////      항상 인코딩 해서 넘겨야 한다. id, 숫자는
////      상관없어서 이번 상황에서는 괜찮지만 이렇게 item.getId()
////      써주는 것은 위험한 것이다.
//    }

    /*
    RedirectAttribute
     */

    /*
    * 상품을 저장하고 상품 상세 화면으로 리다이렉트 한 것 까지는 좋았다. 그런데 고객 입장에서 저장이 잘 된것인지 안된것인지
    * 확신이 들지 않는다. 그래서 저장이 잘 되었으면 상품 상세 화면에 "저장되었습니다" 라는 메시지를 보여달라는 요구사항이 왔다.
    * 간단하게 해결해 보자
    * */

    @PostMapping("/add")
    public String addItemV8(Item item, RedirectAttributes redirectAttributes){
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/basic/items/{itemId}";
        /*
        * redirectAttribute.addAttribute에 key-value를 넣으면 그 넣은 키와 일치하는 변수를 redirect의 경로변수(pathVariable)로
        * 지정할 수 있다. 다시말해 위의 itemId가 {itemId}에 들어간다. 그리고 남은것 ,여기서는 status, 은 쿼리 파라미터 형식으로 들어간다.
        * 그럼 url이 localhost:8080/basic/items/3?status=true 이런식으로 만들어진다.
        * 그리고 pathVariable과 쿼리파라미터를 담고 있는 이 url의 기본적인 인코딩도 전부 해결이 된다.
        *
        * 그럼 클라이언트 브라우저는 localhost:8080/basic/items/3?status=true 을 다시 호출하게 된다. 그럼 item컨트롤러를 통해서
        * item.html이 호출되는데 item컨트롤러에서 status라는 key와 true라는 value를 모델에 담지 않아도 타임리프에서 요청 쿼리파라미터를
        * 직접 꺼낼 수 있는 param을 사용함으로서 사용자로 하여금 저장이 잘 되었는지 알 수 있도록 제어 할 수 있다.
        * item.html에 <h2 th:if="${param.status}" th:text="'저장 완료!'"></h2> 을 추가 함으로서 기능을 구현할 수 있다.
        *
        * th:if  :  해당 조건이 참이면 실행
        * ${param.status}  :   타임리프에서 쿼리 파라미터를 편리하게 조회하는 기능
        *           *  원래는 컨트롤러에서 모델에 직접 담고 값을 꺼냐야 한다. 그런데 쿼리 파라미터는 자주 사용해서 타임리프에서 직접 지원한다.
        *
        * 뷰 템플릿에 메시지를 추가하고 실행해보면 "저장 완료!"라는 메시지가 나오는 것을 확인할 수 있다.
        * 물론 상품 목록에서 상품 상세로 이동한 경우에는 해당 메시지가 출력되지 않는다.
        * */
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
