import unittest
from urllib.error import URLError
from unittest.mock import MagicMock, patch

from ai.services.llm import LlmConnectionError, generate_answer


class LlmServiceTests(unittest.TestCase):

    @patch("ai.services.llm.urlopen")
    def test_generate_answer_returns_ollama_content(self, mock_urlopen: MagicMock) -> None:
        # 실제 Ollama를 호출하지 않고 약속된 JSON 응답만 재현한다.
        mock_response = MagicMock()
        mock_response.read.return_value = (
            b'{"message": {"content": "mock answer"}}'
        )
        mock_urlopen.return_value.__enter__.return_value = mock_response

        self.assertEqual("mock answer", generate_answer("test question"))

    @patch("ai.services.llm.urlopen", side_effect=URLError("connection failed"))
    def test_generate_answer_wraps_connection_error(self, mock_urlopen: MagicMock) -> None:
        # 외부 연결 오류가 API 계층에서 처리할 수 있는 예외로 변환되는지 확인한다.
        with self.assertRaises(LlmConnectionError):
            generate_answer("test question")

        mock_urlopen.assert_called_once()


if __name__ == "__main__":
    unittest.main()
