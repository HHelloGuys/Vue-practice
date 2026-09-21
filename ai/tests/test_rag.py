import unittest

from ai.services.rag import retrieve


class RagServiceTests(unittest.TestCase):

    def test_retrieve_returns_only_related_chunks(self) -> None:
        chunks = [
            {"fileName": "vue.md", "content": "Vue Composition API는 ref를 사용합니다."},
            {"fileName": "oracle.md", "content": "Oracle은 관계형 데이터베이스입니다."},
        ]

        result = retrieve("Vue에서 ref를 어떻게 사용해?", chunks)

        self.assertEqual(1, len(result))
        self.assertEqual("vue.md", result[0].file_name)

    def test_retrieve_returns_empty_when_no_term_matches(self) -> None:
        result = retrieve("JWT 설명", [{"fileName": "vue.md", "content": "Vue 반응성"}])
        self.assertEqual([], result)


if __name__ == "__main__":
    unittest.main()
