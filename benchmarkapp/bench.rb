require 'net/http'
require 'json'
require 'concurrent'
require 'benchmark'

# Configurações
api_url = 'http://localhost:3000'
path = '/books' # Rota de exemplo
num_requests = 1000
output_file = 'tempos.txt' # Nome do arquivo de saída

# Abre o arquivo para escrita
output = File.open(output_file, 'w')

# Método para gerar um corpo de requisição aleatório em JSON
def gerar_corpo_aleatorio
  # Modifique esta lógica para gerar o corpo desejado aleatoriamente
  body = {
    title: "Glauber #{rand(1..100)}",
    author: "nome do livros valor_aleatorio_#{rand(1000)}"
  }
  return body.to_json
end

# Método para fazer uma única solicitação POST e registrar o tempo de resposta
def fazer_requisicao(api_url, path, output)
  uri = URI.parse("#{api_url}#{path}")

  elapsed_time_ms = Benchmark.realtime do
    # Crie uma solicitação POST
    request = Net::HTTP::Post.new(uri)
    request['Content-Type'] = 'application/json'
    request.body = gerar_corpo_aleatorio

    # Faça a solicitação e obtenha o tempo de resposta
    response = Net::HTTP.start(uri.hostname, uri.port) do |http|
      http.request(request)
    end
  end

  # Registre o tempo de resposta no arquivo
  output.puts("#{elapsed_time_ms}")
end

# Benchmark
start_time = Time.now

# Use um ThreadPool para fazer as solicitações POST em paralelo
thread_pool = Concurrent::ThreadPoolExecutor.new(max_threads: 10)

num_requests.times do
  thread_pool.post do
    fazer_requisicao(api_url, path, output)
  end
end

# Aguarde a conclusão de todas as solicitações
thread_pool.shutdown
thread_pool.wait_for_termination

end_time = Time.now

# Feche o arquivo de saída
output.close

# Tempo total
total_time = end_time - start_time
puts "Total time: #{total_time} seconds"

average_response_time = total_time / num_requests
puts "Tempo médio de resposta: #{average_response_time} segundos"