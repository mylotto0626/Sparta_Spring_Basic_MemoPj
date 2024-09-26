package com.sparta.memopj.controller;

import com.sparta.memopj.dto.MemoRequestDto;
import com.sparta.memopj.dto.MemoResponseDto;
import com.sparta.memopj.entity.Memo;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 컨트롤러임을 알림
// 코드 정렬 : ctrl + alt + l
// 생성자 생성 alt enter
@RestController
@RequestMapping("/api")
public class MemoController {

    private final Map<Long, Memo> memoList = new HashMap<>();

    // body 부분에 json 형태로 넘어올 거여서 RequestBody에 RequestDto 넣음
    @PostMapping("/memos")
    public MemoResponseDto createMemo(@RequestBody MemoRequestDto requestDto) {
        // RequestDto -> Entity (db에 저장)
        Memo memo = new Memo(requestDto);

        // Memo Max ID Check
        // id가 중복이 되면 안되니까
        // Collections.max(memoList.keySet()) -> map 의 키를 가져와서 가장 큰 값을 가져옴
        // 가장 큰 값이 0보다 크면 데이터가 하나라도 있는 거니까 +1을 해줘서 중복을 막음
        Long maxId = memoList.size() > 0 ? Collections.max(memoList.keySet()) + 1 : 1;
        memo.setId(maxId);

        // DB 저장 put
        memoList.put(memo.getId(), memo);

        // Entity -> ResponseDto
        MemoResponseDto memoResponseDto = new MemoResponseDto(memo);

        return memoResponseDto;
    }

    // values() 일단 여러개를 가져옴 -> stream() foreach처럼 돌림 -> memo 생성자가 하나씩 나옴 -> toList 리스트 정렬
    @GetMapping("/memos")
    public List<MemoResponseDto> getMemos() {
        // Map To List
        List<MemoResponseDto> responseList = memoList.values().stream()
                .map(MemoResponseDto::new).toList();

        return responseList;
    }

    @PutMapping("/memos/{id}")
    public Long updateMemo(@PathVariable Long id, @RequestBody MemoRequestDto requestDto) {
        // 해당 메모가 db에 존재하는지 확인
        if(memoList.containsKey(id)) {
            // 해당 메모를 가져오기
            Memo memo = memoList.get(id);

            // 가져온 메모를 수정
            memo.update(requestDto);

            return  memo.getId();

        }else{
            throw new IllegalArgumentException("선택한 메모는 존재하지 않습니다");
        }
    }

    @DeleteMapping("/memos/{id}")
    public Long deleteMemo(@PathVariable Long id) {
        // 해당 메모가 DB에 존재하는지 확인하자
        if(memoList.containsKey(id)) {
            // 해당 메모 삭제하기
            memoList.remove(id);
            return id;
        } else {
            throw new IllegalArgumentException("선택한 메모는 존재하지 않습니다.");
        }
    }
}




