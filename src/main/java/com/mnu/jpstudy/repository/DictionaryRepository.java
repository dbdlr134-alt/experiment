package com.mnu.jpstudy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mnu.jpstudy.entity.Dictionary;

// JpaRepository<다룰 엔티티, PK타입>을 상속받으면
// 자동으로 save(), findById(), delete() 같은 코드를 다 만들어줍니다. (마법!)
public interface DictionaryRepository extends JpaRepository<Dictionary, Long> {

    // 이미 DB에 있는 단어인지 확인하기 위해 '한자(kanji)'로 찾는 기능 추가
    boolean existsByKanji(String kanji);
    
    // 한자로 단어 정보 가져오기
    Dictionary findByKanji(String kanji);
}

